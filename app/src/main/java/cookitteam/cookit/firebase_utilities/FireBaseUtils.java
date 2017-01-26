package cookitteam.cookit.firebase_utilities;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Igor on 20-Jan-17.
 */

//this class is used to store all the References to the DB
public class FireBaseUtils
{
    //start refrence static method which allows to get reference of an object
    //inside the FireBase DataBase
    public static DatabaseReference getFireRef(String reference)
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference refDB = db.getReference(reference);

        return refDB;
    }
    //end reference static method



}
