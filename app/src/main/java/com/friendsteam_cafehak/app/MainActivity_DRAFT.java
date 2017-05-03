package com.friendsteam_cafehak.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity_DRAFT extends AppCompatActivity {
    public ImageView imageView;
    public TextView txtBd;
    public TextView txtGender;
    public TextView txtName;
    public TextView txtURL;
    public TextView txtId;
    public TextView txtLocation;

    public MainActivity_DRAFT() {
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        imageView = ((ImageView) findViewById(R.id.imgPhoto));
        txtName = ((TextView) findViewById(R.id.txtName));
        txtURL = ((TextView) findViewById(R.id.txtURL));
        txtGender = ((TextView) findViewById(R.id.txtGender));
        txtBd = ((TextView) findViewById(R.id.txtBd));
        txtLocation = ((TextView) findViewById(R.id.location));
        txtId = ((TextView) findViewById(R.id.User_id));
        if (LoginActivity.flage_login_by) {
            Bundle localBundle = getIntent().getExtras();
            txtURL.setText(localBundle.getString("email"));
            txtName.setText(localBundle.getString("name"));
            txtId.setText(localBundle.getString("id"));
            Picasso.with(getApplicationContext()).load(localBundle.getString("photo")).into(imageView);
        } else {
            GetUserInfo();
        }

    }


    private void GetUserInfo() {
        Bundle bundle = getIntent().getExtras();
        txtId.setText(bundle.getString("idFacebook"));
        txtGender.setText(bundle.getString("gender"));
        txtBd.setText(bundle.getString("birthday"));
        String fullName = bundle.getString("first_name") + bundle.getString("last_name");
        txtName.setText(fullName);
        txtLocation.setText(bundle.getString("location"));
        txtURL.setText(bundle.getString("email"));
        Picasso.with(getApplicationContext()).load(bundle.getString("profile_pic")).into(imageView);
    }


}