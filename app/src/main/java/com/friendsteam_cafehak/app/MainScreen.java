package com.friendsteam_cafehak.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static String[] List = {"C++", "Java", "test", "Python", "Nodejs"};
    public static ArrayList<String> listCourses;
    ImageView imageID;
    ListView listView;
    String uid;
    GoogleApiClient mGoogleApiClient;
    ArrayAdapter<String> adapter = null;
    DB_Helper db;
    TextView username, email;
    boolean firstOne = true;
    private FirebaseAuth mAuth;

    public static ArrayList<String> getListCourses() {
        listCourses = new ArrayList<>(10);
        listCourses.add("C++");
        listCourses.add("Java");
        listCourses.add("testing");
        listCourses.add("Python");
        listCourses.add("Nodejs");
        return listCourses;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DB_Helper(MainScreen.this);
        //main screen
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        imageID = (ImageView) findViewById(R.id.ProfileImage);
        username = (TextView) findViewById(R.id.nameOfUser);
        email = (TextView) findViewById(R.id.emailOfUser);
        //NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //ListView
        renderList();

        //See if He Login By google Or facebook
        if (LoginActivity.flage_login_by) {
            Bundle localBundle = getIntent().getExtras();
            email.setText(localBundle.getString("email"));
            username.setText(localBundle.getString("name"));
            Picasso.with(getApplicationContext()).load(localBundle.getString("photo")).into(imageID);
        } else {
            FirebaseUser user = mAuth.getCurrentUser();
            email.setText(user.getEmail());
            String[] name = db.getnames(user.getEmail());
            username.setText(name[0] + name[1]);
            Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(imageID);
        }

    }


    @Override
    public void onBackPressed() {
        if (firstOne) {
            Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
            firstOne = false;
        } else {
            firstOne = true;
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                //super.onBackPressed();
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
                localBuilder.setTitle("Are you sure to Exit APP ");
                localBuilder.setIcon(R.drawable.alert);
                final AccessToken accessToken = AccessToken.getCurrentAccessToken();

                localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        FirebaseAuth.getInstance().signOut();
                        if (mGoogleApiClient != null) {
                            signOut();
                        }
                        if (accessToken != null)
                            LoginManager.getInstance().logOut();
                        Process.killProcess(Process.myPid());
                        System.exit(1);

                    }
                });
                localBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                localBuilder.show();
            }
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Sign_Out) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle("Sign Out ?");
            localBuilder.setIcon(R.drawable.alert);
            localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainScreen.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            localBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            localBuilder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        try {
            int id = item.getItemId();

            android.app.FragmentManager fragmentManager = getFragmentManager();


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "A");
            return false;
        }
    }

    public void renderList() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            uid = user.getUid();
        if (uid == null) {
            Bundle bundle = getIntent().getExtras();
            uid = bundle.getString("id");
        }
        ArrayList<String> listCourses = db.SelectCourses(uid);
        if (listCourses != null) {
            MainScreen.listCourses = listCourses;
            adapter = new ArrayAdapter<>(MainScreen.this, android.R.layout.simple_list_item_1, listCourses);
            listView = (ListView) findViewById(R.id.list_view_inside_nav);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    First_Fragmint first = new First_Fragmint();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Index", position);
                    bundle.putString("id", uid);
                    first.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.contentFrame, first).commit();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Error Check Your Connection", Toast.LENGTH_LONG).show();
        }
    }


}
