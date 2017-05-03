package com.friendsteam_cafehak.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

/**
 * Created by Mohamed-A.Radwan on 23/09/2016.
 */

public class Normal_Login extends AppCompatActivity {
    private static final String TAG = "TAG";
    private static int userlogedcounter;
    DB_Helper db;
    private EditText Password;
    private EditText USername;
    private FirebaseAuth mAuth;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.normal_login);
        mAuth = FirebaseAuth.getInstance();

    }


    public void onLogin(View paramView) {

        userlogedcounter++;
        Toast.makeText(this, "this is " + userlogedcounter + " try", Toast.LENGTH_LONG).show();
        USername = ((EditText) findViewById(R.id.Email_id));
        Password = ((EditText) findViewById(R.id.Password_id));
        if (userlogedcounter > 3) {
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
        if (USername.length() == 0) {
            USername.setError("Please eneter your Username OR Email");
            return;
        }
        if (Password.length() == 0) {
            Password.setError("Please eneter your Password");
            return;
        }
        try {
            LoginIn(USername.getText().toString(), Password.getText().toString());
            userlogedcounter = 0;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void LoginIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Normal_Login.this, MainScreen.class);
                            db = new DB_Helper(Normal_Login.this);
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean b = db.insertCourse(MainScreen.getListCourses(), user.getUid());
                            startActivity(intent);
                            //finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Normal_Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void OncForgit(View view) {
        USername = (EditText) findViewById(R.id.Email_id);
        if (USername.getText() != null)
            mAuth.fetchProvidersForEmail(USername.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                    if (task.isSuccessful()) {
                        ///////// getProviders() will return size 1. if email ID is available.

                        if (task.getResult().getProviders() != null) {
                            Intent intent = new Intent(Normal_Login.this, ForgitPassword.class);
                            intent.putExtra("email", USername.getText().toString());
                            startActivity(intent);

                        } else {
                            USername.setError("Cant find This USer!");
                        }

                    }
                    if (!task.isSuccessful())
                        Toast.makeText(Normal_Login.this, "Check your network", Toast.LENGTH_SHORT).show();
                }
            });
        else USername.setError("Cant be Empty");
    }
}
