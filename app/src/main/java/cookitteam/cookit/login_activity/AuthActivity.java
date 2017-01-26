package cookitteam.cookit.login_activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cookitteam.cookit.R;
import cookitteam.cookit.firebase_utilities.FireBaseUtils;
import cookitteam.cookit.firebase_utilities.firebase_classes.UserDB;
import cookitteam.cookit.mainpage_activity.MainPageActivity;
import cookitteam.cookit.utilities.Constants;
import cookitteam.cookit.utilities.GenUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AuthActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    //Delcare Firebase auth
    private FirebaseAuth mFireBaseAuth;
    //Start FaceBook

    //declare callbackmanager used only for FB
    private CallbackManager mCallBackManager;
    //Facebook login button
    private LoginButton fbBtnLogin;
    //Finish Facebook

    //Start Google
    //set the google api
    private GoogleApiClient mGoogleApiClient;
    private SignInButton ggleBtnLogin;
    //Finish Google

    //intiate sharedprefrences
    GenUtils utils = new GenUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //method used to initialize facebook SDK
        FacebookSdk.sdkInitialize(AuthActivity.this);
        setContentView(R.layout.activity_main);

        //start intialize Authenticatino
        mFireBaseAuth = FirebaseAuth.getInstance();
        //end intialize Authenticatino

        //start initialize Btns
        fbBtnLogin=(LoginButton)findViewById(R.id.button_facebook_login);
        ggleBtnLogin = (SignInButton)findViewById(R.id.sign_in_button_google);

        //start init google settings
        initGoogleApi();
        //finish init google settings

        //start set progressbar
        utils.setLoadingSetter("Authenticating..." ,false);
        //finish set progressbar

        //google btn onclick
        ggleBtnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginGoogle();
            }
        });

        //start intiazlize Facebook login
        initFacebookLogin();
        //end intiazlize Facebook login

       // checkUser(mFireBaseAuth);

        if(utils.isLogged(Constants.LOGIN_PREF,Constants.CHECK_LOGGED))
        {
            checkUser(mFireBaseAuth);
        }


    }

    //intialize needed resources for google SignIn
    private void initGoogleApi()
    {
        GoogleSignInOptions mGoogleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /*OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleOptions)
                .build();
    }

    private void loginGoogle()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,Constants.GOOGLE_TAG_RC);
    }

    //method used to initialize the facebook login in the onCreate
    private void initFacebookLogin()
    {
        mCallBackManager = CallbackManager.Factory.create();
        fbBtnLogin.setReadPermissions("email", "public_profile");
        fbBtnLogin.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {
                Log.d(Constants.FACEBOOK_TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error)
            {
                Log.d(Constants.FACEBOOK_TAG, "facebook:onError",error);
            }
        });
    }

    //START auth_with_facebook
    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d(Constants.FACEBOOK_TAG, "handleFacebookAccessToken:" + token);
        // start progress dialgo here
        utils.setLoadingGetter().show();
        // end progress dialog
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.d(Constants.FACEBOOK_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful())
                        {
                            // start Ending of progress dialgo here silen
                            utils.setLoadingGetter().dismiss();
                            // end Ending of progress dialgo here silen

                            Log.w(Constants.FACEBOOK_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication with Facebook failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //if successfull get user data and run a check if he signed up in the past
                            utils.saveLoggedUser(Constants.LOGIN_PREF,Constants.CHECK_LOGGED);
                            checkUser(mFireBaseAuth);
                        }
                    }
                });
    }
    //Finish auth_with_facebook

    //Start auth_with_google
    private void handleGoogleLogin(GoogleSignInAccount account)
    {
        Log.e(Constants.GOOGLE_TAG,account.getId());

        //start progress dialog
        utils.setLoadingGetter().show();
        //end progress dialog

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if(!task.isSuccessful())
                        {
                            // start Ending of progress dialgo here silen
                            utils.setLoadingGetter().dismiss();
                            // end Ending of progress dialgo here silen

                            Log.w(Constants.GOOGLE_TAG, "signInWithCredential",task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication with Google failed.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //if successfull get user data and run a check if he signed up in the past
                            utils.saveLoggedUser(Constants.LOGIN_PREF,Constants.CHECK_LOGGED);
                            checkUser(mFireBaseAuth);
                        }
                    }
                });
    }
    //Finish auth_with_google

    //method used to check if the user is writen in the DB
    private void checkUser(FirebaseAuth mFirebaseAuth)
    {
        final String username = mFirebaseAuth.getCurrentUser().getDisplayName();
        final String usermail = mFirebaseAuth.getCurrentUser().getEmail();
        final String userUID = mFirebaseAuth.getCurrentUser().getUid();
        final String userURI = String.valueOf(mFirebaseAuth.getCurrentUser().getPhotoUrl());

        //loadshow
        utils.setLoadingGetter().dismiss();
        utils.setLoadingSetter("Registering..." ,false);
        utils.setLoadingGetter().show();

        FireBaseUtils
                .getFireRef(Constants.USER_DB)
                .child(userUID)
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        //if doesnt exists add to DB
                        if(dataSnapshot.exists())
                        {
                            utils.setLoadingGetter().dismiss();
                            toMain();
                        }
                        else
                        {
                            UserDB user = new UserDB(username,usermail,userUID,userURI);
                            user.userToDb();
                            utils.setLoadingGetter().dismiss();
                            toMain();
                        }

                        //load dimiss
                        utils.setLoadingGetter().dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        utils.setLoadingGetter().dismiss();
                        Toast.makeText(AuthActivity.this, "Connection to server failed", Toast.LENGTH_SHORT).show();
                        Log.e("checkUserDBAUTH",databaseError + "");
                    }
                });
    }

    private void toMain()
    {
        startActivity(new Intent(AuthActivity.this, MainPageActivity.class));
        finish();
    }

    //START on_activity_result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.GOOGLE_TAG_RC)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                handleGoogleLogin(account);
            }
        }
        else
        {
            // Pass the activity result back to the Facebook SDK
            mCallBackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this, "Login with Google Failed", Toast.LENGTH_SHORT).show();
    }
    //END on_activity_result

}

//method to get hashkey for FB AUthentication in FB Developer
//use this method to get your private key for your device

    /*"This command will generate a 28-character key hash unique to your development environment.
    Copy and paste it into the field below. You will need to provide a
    development key hash for the development environment of each person who works on your app."
    Facebook developer page*/
   /* public void getkey()
    {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("cookitteam.cookit", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key", something);//<-- Your hashkey will apear  in the console!
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }*/