package cookitteam.cookit.firebase_utilities.firebase_classes;

import android.support.annotation.Keep;

import cookitteam.cookit.firebase_utilities.FireBaseUtils;
import cookitteam.cookit.utilities.Constants;

/**
 * Created by Igor on 20-Jan-17.
 */

//this  class is used to upload User
@Keep//<--important for later (production versions)
public class UserDB
{
    //basic drawn data from the user credential
     private String userName,userMail,userUID,strBitmap;
     private int userScore;

     public UserDB(String userName,String userMail,String userUID, String strBitmap)
     {
         this.userName = userName;
         this.userMail = userMail;
         this.strBitmap = strBitmap;
         this.userUID = userUID;
         this.userScore = 0;
     }

    public String getUserName()
    {
        return userName;
    }

    public String getUserMail()
    {
        return userMail;
    }

    public String getStrBitmap()
    {
        return strBitmap;
    }

    public String getUserUID() {
        return userUID;
    }

    public int getUserScore() {
        return userScore;
    }

    //this method uploads data into FirebaseDB
    public void userToDb()
    {
        UserDB user = new UserDB(userName,userMail,userUID,strBitmap);
        FireBaseUtils.getFireRef(Constants.USER_DB).child(getUserUID()).setValue(user);
    }
}
