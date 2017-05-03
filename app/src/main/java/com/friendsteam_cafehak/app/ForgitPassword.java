package com.friendsteam_cafehak.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ForgitPassword extends AppCompatActivity {
    EditText fname, lname, email, password, co_password;
    CheckBox fBox, lBox;
    DB_Helper db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_forgit_password);
        fname = (EditText) findViewById(R.id.First_name);
        lname = (EditText) findViewById(R.id.Last_Name);
        email = (EditText) findViewById(R.id.Email_id);
        password = (EditText) findViewById(R.id.Password_id);
        co_password = (EditText) findViewById(R.id.Password_confirm);
        fBox = (CheckBox) findViewById(R.id.checkfirst);
        lBox = (CheckBox) findViewById(R.id.checklast);
        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();
        final String Semail = bundle.getString("email");
        email.setText(Semail);
        email.setEnabled(false);
        lBox.setEnabled(false);
        fBox.setEnabled(false);
        db = new DB_Helper(this);
        String[] s = db.getnames(Semail);

        if (s.length != 0) {
            final String firstname = s[0];
            Toast.makeText(getApplicationContext(), firstname, Toast.LENGTH_LONG);
            final String lastname = s[1];
            fname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().equals(firstname)) {
                        fBox.setChecked(true);
                        if (fBox.isChecked() && fBox.isChecked()) {
                            Toast.makeText(getApplicationContext(), "REset Email sended to your email", Toast.LENGTH_LONG);
                            mAuth.sendPasswordResetEmail(Semail);
                        } else {
                            fBox.setChecked(false);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            lname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().equals(lastname)) {
                        lBox.setChecked(true);
                        if (lBox.isChecked() && fBox.isChecked()) {
                            Toast.makeText(ForgitPassword.this, "REset Email sended to your email", Toast.LENGTH_LONG);
                            mAuth.sendPasswordResetEmail(Semail);

                        }
                    } else {
                        lBox.setChecked(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } else {
            Toast.makeText(this, "ERROR had Happen please connect to developer", Toast.LENGTH_SHORT).show();
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

    }
}
