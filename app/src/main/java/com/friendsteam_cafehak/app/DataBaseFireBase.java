package com.friendsteam_cafehak.app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Mohamed-A.Radwan on 29/04/2017 and 10:23 PM.
 */

public class DataBaseFireBase {
    static boolean flag = false;
    private static FirebaseDatabase database;
    private static DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static void Initilize() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
        }
    }

    public static void addListOFCourses(String userID) {
        Initilize();
        myRef.child("users").child(userID).child("ListOfCourses").setValue(MainScreen.getListCourses());
    }

    public static void RegisterUser(FirebaseUser user, Map<String, String> userInfo) {
        Initilize();
        myRef.child("users").child(user.getUid()).child("userInfo").setValue(userInfo);
        addListOFCourses(user.getUid());
    }

    public static ArrayList<String> getListFromDatabase(String userID) {
        DatabaseReference queryRef = FirebaseDatabase.getInstance().getReference("users");
        ArrayList<String> Cources = null;
        queryRef.child(userID).orderByChild("ListOfCourses").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object cp = dataSnapshot.getValue();
                ArrayList<String> Cources = (ArrayList<String>) cp;
                flag = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return Cources;
    }
}
