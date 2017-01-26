package cookitteam.cookit.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Igor on 19-Jan-17.
 */

public class GenUtils
{
    //context and sharedPref delcarations
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    //progress dialog delcarations
    private ProgressDialog mProgresssDialog;

    public GenUtils(Context context)
    {
        this.context = context;
    }

    //method for initializing shared prefrences
    //via using a String Delcartion for the path you wish to svae them in!
    //aka [prefFile]
    private void initPrefs(String prefFile)
    {
        prefs = context.getApplicationContext().getSharedPreferences(prefFile,0);
        editor = prefs.edit();
    }

    //this method is used to insert a boolean value
    //after user has intialially logged in with his account
    public void saveLoggedUser(String prefFile,String keyValue)
    {
        //used in this method to initialize the prefs and choose filePath
        initPrefs(prefFile);
        // inserts true into chosen keyValue and commits
        editor.putBoolean(keyValue,true).commit();
    }

    //checks if user is marked in the Prefs as first log in!
    public boolean isLogged(String prefFile,String keyValue)
    {
        initPrefs(prefFile);
        boolean checkUser = prefs.getBoolean(keyValue,false);
        return checkUser;
    }

    public void setLoadingSetter(String loadingText,boolean isCancelable)
    {
        mProgresssDialog = new ProgressDialog(context);
        mProgresssDialog.setMessage(loadingText);
        mProgresssDialog.setIndeterminate(true);
        mProgresssDialog.setCancelable(isCancelable);
    }

    public ProgressDialog setLoadingGetter()
    {
        return mProgresssDialog;
    }

    //checks if connection to the Internet is availble
    //should be used to determine if user can log in or not // continue with the app
    public static boolean isNetworkEnabled(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwrok = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwrok != null && activeNetwrok.isConnectedOrConnecting();
        return isConnected;
    }

}
