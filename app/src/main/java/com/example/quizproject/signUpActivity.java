package com.example.quizproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class signUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailSignUp,passwordSignUp,verifyPasswordSignUp,userNameSignUp;
    TextView tvFails;
    Button btnSignUp,btnSignIn;
    ImageButton userPictureImageButton;
    private FirebaseAuth mAuth;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    Bitmap bm;
    Dialog loadingpage;
    StorageReference storageRef;
    FirebaseStorage storage;
    boolean haveImage;
    Uri urlPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailSignUp=findViewById(R.id.emailSignUp);
        userNameSignUp=findViewById(R.id.userNameSignUp);
        passwordSignUp=findViewById(R.id.passwordSignUp);
        verifyPasswordSignUp=findViewById(R.id.verifyPasswordSignUp);
        userPictureImageButton=findViewById(R.id.userPictureImageButton);
        btnSignUp=findViewById(R.id.btnToSignUp);
        btnSignIn=findViewById(R.id.btnReturnSignIn);
        tvFails=findViewById(R.id.tvFails);
        btnSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        userPictureImageButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        sp=getSharedPreferences("info",0);
        edit=sp.edit();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    @Override
    public void onClick(View v) {
        if (btnSignUp == v){
            String email, password, verifiedPassword,userName;
            email = emailSignUp.getText().toString();
            password = passwordSignUp.getText().toString();
            verifiedPassword = verifyPasswordSignUp.getText().toString();
            userName=userNameSignUp.getText().toString();
            if (email.equals("") || password.equals("") || verifiedPassword.equals("")) { // בודק תקינות פרטים
                tvFails.setText("you have to enter all data");
            }
            else {
                if (verifiedPassword.equals(password)){
                    if (email.indexOf("@")>-1) {// בודק תקינות אימייל
                       loadingpage=new Dialog(this);
                       loadingpage.setContentView(R.layout.loading_layout);
                        loadingpage.setCancelable(false);
                       loadingpage.show();
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //user is updated in auth
                                            //update picture
                                            if (haveImage){//ממיר את התמונה לביטים
                                                StorageReference pictursRef = storageRef.child("profilePicturs/"+mAuth.getCurrentUser().getEmail()+"profilepicture.jpg");
                                                userPictureImageButton.setDrawingCacheEnabled(true);
                                                userPictureImageButton.buildDrawingCache();
                                                Bitmap bitmap = ((BitmapDrawable) userPictureImageButton.getBackground()).getBitmap();
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] data = baos.toByteArray();
                                                UploadTask uploadTask = pictursRef.putBytes(data);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {//מוריד את התמונה לסטורג
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(signUpActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
                                                        urlPicture=null;
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Toast.makeText(signUpActivity.this, "Your profile picture has changed updated", Toast.LENGTH_LONG).show();
                                                        //העלינו את התמונה עכשיו

                                                        //נשמור את התמונה בתוך auth של כל יוזר
                                                        pictursRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                setUserNameAndPicture(userName,uri);

                                                            }
                                                        });
                                                       // setUserNameAndPicture(userName,urlPicture); because need the download to finish

                                                    }
                                                });
                                            }
                                            else{
                                                urlPicture=Uri.parse("android.resource://com.example.quizproject/" + R.drawable.userimage);
                                                setUserNameAndPicture(userName,urlPicture);
                                            }
                                        }
                                        else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(signUpActivity.this, "לא הצלחת להירשם נסה שוב", Toast.LENGTH_SHORT).show();
                                            loadingpage.cancel();
                                        }
                                    }
                                });
                    }
                    else{
                        tvFails.setText("you have to enter appropriate email");
                    }
                }
                else{
                    tvFails.setText("you have to enter the same passwords");
                }
            }
        }
        else if (btnSignIn==v){
            Intent it=new Intent(signUpActivity.this,signInActivity.class);
            startActivity(it);
        }
        else if (userPictureImageButton == v){

            Intent it=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(it,0);
        }
    }
 /*   public void downloadAnImage(String userName){
        storageRef.child("profilePicturs/"+mAuth.getCurrentUser().getEmail()+"profilepicture.jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String bitMaps=sp.getString("bitMaps","");
                String bitMap=BitMapToString(bm);
                bitMaps=bitMaps+bitMap+"#";
                edit.putString("bitMaps",bitMaps);
                edit.commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        setUserNameAndPicture(userName,Uri.parse("aaa"));
    }*/
    public void setUserNameAndPicture(String userName,Uri userImageUrl){
        urlPicture=userImageUrl;
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(userImageUrl)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db= FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("Users").document("usersDetail");//הכנסת הפרטים של המשתמש כולל אימייל נקודות ושם משתמש
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> userDetails=document.getData();
                                            User NewUser=new User(user.getDisplayName(),user.getEmail());
                                            NewUser.setProfileImageUri(urlPicture);//יהיה רשום גם לכל משתמש את הכתובת של התמונת פרופיל שלו בשביל לגשת בטבלת הזוכים
                                            userDetails.put(""+mAuth.getCurrentUser().getEmail(),NewUser);
                                            db.collection("Users").document("usersDetail")
                                                    .set(userDetails)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //put in sharedPref
                                                            String emails=sp.getString("emails","");//מוסיף את האימייל והנקודות לshered כי התמונה נוספה כבר
                                                            String points=sp.getString("points","");
                                                            emails=emails+NewUser.getEmail()+"#";
                                                            points=points+NewUser.getPoints()+"#";
                                                            edit.putString("emails",emails);
                                                            edit.putString("points",points);
                                                            edit.commit();
                                                            loadingpage.cancel();
                                                            Toast.makeText(signUpActivity.this, "ההרשמה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                                                            Intent it=new Intent(signUpActivity.this,MainActivity.class);
                                                            startActivity(it);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(signUpActivity.this,"אוי חבל לא עבד",Toast.LENGTH_SHORT).show();
                                                            loadingpage.cancel();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(signUpActivity.this, "משהו השתבש נסה שוב", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) //
        {
            if(resultCode == RESULT_OK)
            {
                Uri uri=data.getData();
                bm=(Bitmap)data.getExtras().get("data");
                BitmapDrawable ob = new BitmapDrawable(getResources(), bm);
                userPictureImageButton.setBackground(ob);
                Toast.makeText(this, "Your profile picture has changed successfully", Toast.LENGTH_LONG).show();
                haveImage=true;
            }
            else
                Toast.makeText(this, "No picture was taken", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        menu.findItem(R.id.mainPage).setVisible(true);
        menu.findItem(R.id.leaderboard).setVisible(false);
        menu.findItem(R.id.disconnect).setVisible(false);
        menu.findItem(R.id.signInPage).setVisible(false);
        menu.findItem(R.id.signUpPage).setVisible(false);
        menu.findItem(R.id.profileStatus).setVisible(false);
        menu.findItem(R.id.aboutPage).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.mainPage) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.aboutPage) {
            intent = new Intent(this, AboutProjectActivity.class);
            startActivity(intent);
        }

        return true;
    }
}