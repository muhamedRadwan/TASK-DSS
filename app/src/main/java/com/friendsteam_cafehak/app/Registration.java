package com.friendsteam_cafehak.app;

/**
 * Created by Mohamed-A.Radwan on 25/08/2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;


public class Registration extends AppCompatActivity {
    private static final String TAG = "TAG";
    private static final int PICK_IMAGE = 1;
    private static final int CapImage = 2;
    ImageView imageView;
    DB_Helper db;
    RoundImage roundedImage;
    String password, confirmPassword, email, Lname, Fname;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public Registration() {
    }


    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        db = new DB_Helper(this);
        setContentView(R.layout.registration_form);
        Spinner localSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter localArrayAdapter = ArrayAdapter.createFromResource(this, R.array.planets_array, R.layout.support_simple_spinner_dropdown_item);
        localArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        localSpinner.setAdapter(localArrayAdapter);
        localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
                ((TextView) paramAdapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.titleBarColor));
                ((TextView) paramAdapterView.getChildAt(0)).setTextSize(26.0F);
            }

            public void onNothingSelected(AdapterView<?> paramAdapterView) {

            }
        });
        mAuth = FirebaseAuth.getInstance();
        imageView = (ImageView) findViewById(R.id.ProfileImage);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
        imageView.setImageBitmap(bm);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openChoiceImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        RoundImage roundedImage;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CapImage) {
                bitmap = (Bitmap) data.getExtras().get("data");
                bitmap = getCroppedBitmap(bitmap);
                imageView.setImageBitmap(bitmap);
            } else {
                if (requestCode == PICK_IMAGE) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    bitmap = BitmapFactory.decodeFile(filePath);
                    bitmap = getCroppedBitmap(bitmap);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    imageView.setImageDrawable(d);


                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void regest(View paramView) {

        EditText editPassword, editconfirmPassword, editFname, editLname, editEmail;
        TextInputLayout LayEmail = (TextInputLayout) findViewById(R.id.TextLayoutEmail);
        TextInputLayout LayFname = (TextInputLayout) findViewById(R.id.TextLayoutFname);
        TextInputLayout LayLname = (TextInputLayout) findViewById(R.id.TextLayoutLname);
        TextInputLayout LaySpinner = (TextInputLayout) findViewById(R.id.TextLayoutSpinner);
        TextInputLayout LayPassword = (TextInputLayout) findViewById(R.id.TextLayoutPassword);
        TextInputLayout LayCPassword = (TextInputLayout) findViewById(R.id.TextLayoutCPassword);
        editFname = (EditText) findViewById(R.id.First_name);
        editLname = (EditText) findViewById(R.id.Last_Name);
        editEmail = (EditText) findViewById(R.id.Email_id);
        editPassword = (EditText) findViewById(R.id.Password_id);
        editconfirmPassword = (EditText) findViewById(R.id.Password_confirm);
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();
        confirmPassword = editconfirmPassword.getText().toString();
        Lname = editLname.getText().toString();
        Fname = editFname.getText().toString();


        Validations localValidation = Validations.Get_Validations();
        if (!localValidation.Is_email(email)) {
            LayEmail.setError(getText(R.string.valid_email));
            resetBackGround(LayEmail);
            editEmail.requestFocus();
        } else if (!localValidation.Is_alpha(Fname)) {
            LayEmail.setErrorEnabled(false);
            LayFname.setError(getText(R.string.valid_name));
            resetBackGround(LayFname);
            editFname.requestFocus();
        } else if (!localValidation.Is_alpha(Lname)) {
            LayFname.setErrorEnabled(false);
            LayLname.setError(getText(R.string.valid_name));
            resetBackGround(LayLname);
            editLname.requestFocus();
        } else if (!localValidation.Is_passord(password)) {
            LayLname.setErrorEnabled(false);
            LayPassword.setError(getText(R.string.valid_password));
            resetBackGround(LayPassword);
            editPassword.requestFocus();

        } else if (!password.equals(confirmPassword)) {
            LayPassword.setErrorEnabled(false);
            LayPassword.setError(getText(R.string.confirm_password));
            if (editconfirmPassword != null) {
                LayCPassword.setError(getText(R.string.confirm_password));
                resetBackGround(LayCPassword);
                resetBackGround(LayPassword);
            }
            editPassword.setText("");
            if (editconfirmPassword != null) {
                editconfirmPassword.setText("");
            }
            editPassword.requestFocus();
        } else {
            LayCPassword.setErrorEnabled(false);
            LayPassword.setErrorEnabled(false);
            registerToFirebase(email, password);
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void registerToFirebase(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getException() != null)
                                Toast.makeText(Registration.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            else {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Map<String, String> UserInfo = new ArrayMap<>(10);
                                UserInfo.put("Fullname", Fname + Lname);
                                UserInfo.put("email", email);
                                UserInfo.put("id", user.getUid());
                                boolean b = db.insertCourse(MainScreen.getListCourses(), user.getUid());
                                boolean S = db.insertname(Fname, Lname, email, user.getUid());
                                DataBaseFireBase.RegisterUser(user, UserInfo);
                                Intent intent = new Intent(Registration.this, MainScreen.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(Registration.this, "Cannot Regist " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    protected void resetBackGround(TextInputLayout layout) {
        layout.getEditText().getBackground()
                .setColorFilter(
                        ContextCompat.getColor(getApplicationContext(), R.color.light_brown),
                        PorterDuff.Mode.SRC_IN
                );
    }

    protected void openChoiceImage() {
        final Dialog dialogChoose = new Dialog(this);
        dialogChoose.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChoose.setContentView(R.layout.image_choice);
        Button buGallery = (Button) dialogChoose.findViewById(R.id.BuGallery);
        Button buCam = (Button) dialogChoose.findViewById(R.id.BuCam);
        buGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
                dialogChoose.dismiss();
            }
        });
        buCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CapCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(CapCam, CapImage);
                dialogChoose.dismiss();
            }
        });
        dialogChoose.show();
    }
}
