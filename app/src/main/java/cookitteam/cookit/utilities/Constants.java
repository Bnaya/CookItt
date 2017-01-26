package cookitteam.cookit.utilities;

/**
 * Created by Igor on 19-Jan-17.
 */

//this class is used to store all of the constants of the application therefor all constants will be stored HERE
public class Constants
{
    //start AuthActivity constants
    public static String FACEBOOK_TAG="FacebookLogin";
    public static String GOOGLE_TAG="GoogleLogin";
    public static int GOOGLE_TAG_RC = 9001;//<-- requested by GOOGLE API to login
    //end AuthActivity constants

    //start SharedPrefrences Constants
    //XML File path
    public static final String LOGIN_PREF = "LoginXML";
    //prefrences key values
    public static final String CHECK_LOGGED = "isLoggedOnce";
    //finish SharedPrefrences Constants

    //start Firebase Database References
    public static String USER_DB="UserDB";
    //finish Firebase Database References
}
