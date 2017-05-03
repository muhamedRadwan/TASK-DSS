package com.friendsteam_cafehak.app;

/**
 * Created by Mohamed-A.Radwan on 25/08/2016 and 07:19 PM and 06:45 PM and 06:45 PM.
 */

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = LoginActivity.class.getName();
    public static boolean flage_login_by = false;
    public static DB_Helper db;
    SignInButton localSignInButton;
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;


    public LoginActivity() {

    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.login_activity);
        db = new DB_Helper(LoginActivity.this);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent localIntent = new Intent(getApplicationContext(), MainActivity_DRAFT.class);
                startActivity(localIntent);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("gevna", "callbackManager error");
            }
        });
        this.accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };


        customizerSignBtu();
        GoogleSignInOptions localGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, localGoogleSignInOptions)
                .build();
        customizerSignBtu();
        localSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                LoginActivity.flage_login_by = true;
                Intent localIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(localIntent, RC_SIGN_IN);
            }
        });


        change_status_bar();
    }

    public void onClick(View paramView) {
        if (paramView.getId() == R.id.login_button) {
            flage_login_by = true;
            login_face();
        } else if (paramView.getId() == R.id.login) {
            Intent intent = new Intent(LoginActivity.this, Normal_Login.class);
            startActivity(intent);
            finish();
        }

    }

    public void login_face() {
        final LoginButton localLoginButton = (LoginButton) findViewById(R.id.login_button);
        if (localLoginButton != null) {
            localLoginButton.setReadPermissions(Arrays.asList(new String[]{"user_friends", "email", "user_birthday", "user_location"}));
        }
        this.accessTokenTracker.startTracking();
        if (localLoginButton != null) {
            localLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    final Intent localIntent = new Intent(LoginActivity.this, MainScreen.class);
                    AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                    mAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Bundle bFacebookData = getFacebookData(object);
                                    localIntent.putExtras(bFacebookData);
                                    startActivity(localIntent);
                                    LoginActivity.this.finish();

                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                    });
                }

                @Override
                public void onCancel() {
                    // App code
                    Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Toast.makeText(getApplicationContext(), "Eroor" + exception.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            FirebaseUser user = mAuth.getCurrentUser();
            String id = object.getString("id");
            boolean b = db.insertCourse(MainScreen.getListCourses(), user.getUid());
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                bundle.putString("photo", profile_pic.toString());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("id", id);
            if (object.has("last_name") && object.has("first_name"))
                bundle.putString("name", object.getString("last_name") + object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Get Google Person Data
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            try {
                GoogleSignInAccount account = result.getSignInAccount();
                handleSignInResult(account);
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                        } else if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();

                        }
                    }

                });
    }

    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult) {

        Toast.makeText(getApplicationContext(), "connection filled", Toast.LENGTH_LONG).show();
    }

    protected void onStart() {
        super.onStart();
        // mAuth.addAuthStateListener(mAuthListener);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        FirebaseUser user = mAuth.getCurrentUser();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else if (accessToken != null) {
            login_face();
        } else if (user != null) {
            Bundle bundle = new Bundle();
            bundle.putString("name", user.getDisplayName());
            bundle.putString("email", user.getEmail());
            bundle.putString("photo", user.getPhotoUrl().toString());
            Intent intent = new Intent(this, MainScreen.class);
            flage_login_by = false;
            intent.putExtras(bundle);
            startActivity(intent);
            finish();

        } else {
            // not signed in
        }

    }

    public void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        super.onStop();
        this.accessTokenTracker.stopTracking();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void regist(View paramView) {
        startActivity(new Intent(this, Registration.class));
    }


    private void handleSignInResult(GoogleSignInAccount localGoogleSignInAccount) {
        try {
            if (!localGoogleSignInAccount.equals(null)) {
                Toast.makeText(getApplicationContext(), "" + localGoogleSignInAccount.getDisplayName(), Toast.LENGTH_LONG).show();
                String id = localGoogleSignInAccount.getId();
                db.insertCourse(MainScreen.getListCourses(), id);
                Intent localIntent = new Intent(this, MainScreen.class);
                Bundle localBundle = new Bundle();
                localBundle.putString("email", localGoogleSignInAccount.getEmail());
                localBundle.putString("name", localGoogleSignInAccount.getDisplayName());
                localBundle.putString("id", id);
                localBundle.putString("photo", localGoogleSignInAccount.getPhotoUrl().toString());
                localIntent.putExtras(localBundle);
                startActivity(localIntent);
                LoginActivity.this.finish();
                updateUI(true);
            } else {
                updateUI(false);
            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "Please check your connection to internet", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI(boolean paramBoolean) {
        if (paramBoolean) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            return;
        }

        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
    }


    private void customizerSignBtu() {

        localSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        if (localSignInButton != null) {
            localSignInButton.setSize(SignInButton.SIZE_STANDARD);
            setGooglePlusButtonText(localSignInButton, R.string.signGoogleButton);
        }

    }

    protected void setGooglePlusButtonText(SignInButton signInButton,
                                           int buttonTextID) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(20);
                tv.setText(R.string.signGoogleButton);
                return;
            }
        }
    }

    public void onBackPressed() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(LoginActivity.this);
        localBuilder.setTitle("Are you sure to Exit APP ");
        localBuilder.setIcon(R.drawable.alert);
        localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void change_status_bar() {
        Window window = LoginActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.titleBarColor));

    }


}