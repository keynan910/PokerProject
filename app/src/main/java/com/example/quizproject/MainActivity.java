package com.example.quizproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.SupportActionModeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.PathInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvExplainQuiz, nameAndChooseQuiz;
    Dialog explainQuizDialog;
    ImageButton foodQuizImageButton,pokerGameImageButton;
    Button btnFinishDialog, btnBackDialog, btnSignUp, btnSignIn, btnDisconnect;
    String chosenQuizName;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    SharedPreferences sp;
    boolean profileStatusOn=false;
    SharedPreferences.Editor edit;
    FirebaseFirestore db;
    int num=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db = FirebaseFirestore.getInstance();
        //check first time
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        if (settings.getBoolean("my_first_time", true)) {
            sp = getSharedPreferences("info", 0);
            edit = sp.edit();
            edit.putString("emails", "");
            edit.putString("points", "");
            settings.edit().putBoolean("my_first_time", false).commit();
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
        }
        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("info", 0);
        foodQuizImageButton = findViewById(R.id.ib1);
        foodQuizImageButton.setOnClickListener(this);
        //
        pokerGameImageButton = findViewById(R.id.ib2);
        pokerGameImageButton.setOnClickListener(this);
        //
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        nameAndChooseQuiz = findViewById(R.id.nameAndChooseQuiz);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(this);
        user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            nameAndChooseQuiz.setTextSize(30);
            if (name.matches("[a-zA-Z0-9]+")) {
                nameAndChooseQuiz.setText("Hey " + name + " choose a game");
            } else {
                nameAndChooseQuiz.setText("היי " + name + " בחר משחק");

            }
            btnDisconnect.setVisibility(View.VISIBLE);
            ///////create listener to notifications

        } else {
            btnDisconnect.setVisibility(View.GONE);
            nameAndChooseQuiz.setText("הירשם ולאחר מכן בחר משחק");
        }



    }

    @Override
    public void onClick(View v) {
        user = mAuth.getCurrentUser();
        if (foodQuizImageButton == v && user != null) {
            chosenQuizName = "food";
            explainQuizDialog = new Dialog(this);
            explainQuizDialog.setContentView(R.layout.quizdialog);
            explainQuizDialog.setCancelable(false);
            btnFinishDialog = explainQuizDialog.findViewById(R.id.btnFinishDialog);
            btnBackDialog = explainQuizDialog.findViewById(R.id.btnBackDialog);
            tvExplainQuiz = explainQuizDialog.findViewById(R.id.tvExplainQuiz);
            tvExplainQuiz.setText("חידון האוכל מתבצע כך שהשחקן מתחיל עם 3 לבבות לאחר שהשחקן טעה בשלושה שאלות המשחק נגמר. בכל שאלה יש אפשרות לזכות במספר נקודות שונה, ככל שהתשובה נענת יותר מהר כך מספר הנקודות על השאלה עולה בסוף המשחק יופיע מספר הנקודות שצבר השחקן ומה שיא נקודותיו בחידון האוכל");
            btnFinishDialog.setOnClickListener(this);
            btnBackDialog.setOnClickListener(this);
            explainQuizDialog.show();
        } else if (btnSignIn == v) {
            if (user == null) {
                Intent it;
                it = new Intent(this, signInActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(this, " אתה מחובר כבר" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        } else if (btnSignUp == v) {
            if (user == null) {
                Intent it;
                it = new Intent(this, signUpActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(this, " אתה מחובר כבר" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        } else if (btnBackDialog == v) {
            explainQuizDialog.dismiss();
        } else if (v == btnFinishDialog) {
            Intent it = new Intent();
            if (chosenQuizName.equals("food")) {
                it = new Intent(this, foodActivity.class);
                startActivity(it);
            }
        } else if (btnDisconnect == v) {
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
            nameAndChooseQuiz.setTextSize(20);
            nameAndChooseQuiz.setText("הירשם ולאחר מכן בחר נושא לחידון");
            btnDisconnect.setVisibility(View.GONE);
        }
        else if (pokerGameImageButton==v && user != null){
            Intent it;
            it = new Intent(this, pokerGameFindTable.class);
            startActivity(it);


        }
    }

    //ActionBar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        if (user != null) {
            menu.findItem(R.id.mainPage).setVisible(false);
            menu.findItem(R.id.leaderboard).setVisible(true);
            menu.findItem(R.id.disconnect).setVisible(true);
            menu.findItem(R.id.signInPage).setVisible(false);
            menu.findItem(R.id.signUpPage).setVisible(false);
            menu.findItem(R.id.profileStatus).setVisible(true);
            menu.findItem(R.id.aboutPage).setVisible(true);

        } else {
            menu.findItem(R.id.mainPage).setVisible(false);
            menu.findItem(R.id.leaderboard).setVisible(false);
            menu.findItem(R.id.disconnect).setVisible(false);
            menu.findItem(R.id.signInPage).setVisible(true);
            menu.findItem(R.id.signUpPage).setVisible(true);
            menu.findItem(R.id.profileStatus).setVisible(false);
            menu.findItem(R.id.aboutPage).setVisible(true);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.signInPage) {
            intent = new Intent(this, signInActivity.class);
            startActivity(intent);
        } else if (id == R.id.signUpPage) {
            intent = new Intent(this, signUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.leaderboard) {
            intent = new Intent(this, winnersActivity.class);
            intent.putExtra( "points",-1);
            startActivity(intent);
        } else if (id == R.id.mainPage) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.aboutPage) {
            intent = new Intent(this, AboutProjectActivity.class);
            startActivity(intent);
        }else if (id == R.id.disconnect) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            mAuth.signOut();
            Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.profileStatus) {
            if (mAuth.getCurrentUser() !=null) {
                if (!profileStatusOn) {
                    LinearLayout linearLayoutProfileStatus = findViewById(R.id.LinearLayoutProfileStatus);
                    TextView userNameProfileStatus = linearLayoutProfileStatus.findViewById(R.id.userNameProfileStatus);
                    TextView emailProfileStatus = linearLayoutProfileStatus.findViewById(R.id.emailProfileStatus);
                    TextView pointsProfileStatus = linearLayoutProfileStatus.findViewById(R.id.pointsProfileStatus);
                    ImageView imageProfileStatus = linearLayoutProfileStatus.findViewById(R.id.imageProfileStatus);
                    userNameProfileStatus.setText(user.getDisplayName());
                    emailProfileStatus.setText(user.getEmail());
                    //TODO EXPLAIN בודק קודם אם לאימייל יש כתובת של תמונה וכתובת זו היא סתם כדי לבדוק אם יש תמונה היא לא הכתובת האמיתית ואז מוצא לאימייל את התמונה שלו מהשרד פרפרנסס ושם
                    //TODO אם עשיתי LOG INואין משתמש כזה אז נפתח את הUSERS
                    Glide.with(this).load(user.getPhotoUrl()).into(imageProfileStatus);
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    imageProfileStatus.setMinimumHeight(dm.heightPixels);
                    imageProfileStatus.setMinimumWidth(dm.widthPixels);
                    //find points using sharedprefrences
                    String emails = sp.getString("emails", "");
                    String points = sp.getString("points", "");
                    String[] emailsArr = emails.split("#");
                    String[] pointsArr = points.split("#");
                    int index = Arrays.asList(emailsArr).indexOf("" + user.getEmail());
                    if (index!=-1) {
                        if (Integer.parseInt(pointsArr[index]) != -1) {
                            pointsProfileStatus.setText("" + pointsArr[index]);
                        } else {
                            pointsProfileStatus.setText("no points yet");
                        }
                    }
                    else{//אם עשינו התחברות למשתמש שלא נוצר כאן אז ניקח את הפרטים שלו מהUSERS
                        FirebaseFirestore db;
                        Dialog loadingpage=new Dialog(MainActivity.this);
                        loadingpage.setContentView(R.layout.loading_layout);
                        loadingpage.setCancelable(false);
                        loadingpage.show();
                        db=FirebaseFirestore.getInstance();
                        DocumentReference docRef1 = db.collection("Users").document("usersDetail");
                        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {//פותח את המסמך של המשתמשים
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        if (document.getData().get(user.getEmail())!=null) {
                                            Map<String, Object> documentUserHash = new HashMap<>();
                                            documentUserHash = document.getData();
                                            Map<String, Object> UserHash = (Map) documentUserHash.get(mAuth.getCurrentUser().getEmail());
                                            if (Integer.parseInt("" + UserHash.get("points")) != -1) {
                                                pointsProfileStatus.setText("" + UserHash.get("points"));
                                            } else {
                                                pointsProfileStatus.setText("no points yet");
                                            }
                                            loadingpage.cancel();
                                            //תוך כדי ברקע אז נוסיף את המשתמש לSHERED כדי שפעם הבאה יהיה יותר קל לגשת
                                            //  תזכורת כדי להוסיף לSHERED צריך את הכל גם אימייל וגם נקודות
                                            String emails=sp.getString("emails","");//מוסיף את האימייל והנקודות לshered כי התמונה נוספה כבר
                                            String points=sp.getString("points","");
                                            emails=emails+user.getEmail()+"#";
                                            points=points+UserHash.get("points")+"#";
                                            edit=sp.edit();
                                            edit.putString("emails",emails);
                                            edit.putString("points",points);
                                            edit.commit();
                                        }
                                    }
                                }
                            }
                        });
                    }
                    ObjectAnimator animation = ObjectAnimator.ofFloat(linearLayoutProfileStatus, "translationX", -330f);
                    linearLayoutProfileStatus.setVisibility(View.VISIBLE);
                    animation.start();
                    ObjectAnimator animation2 = ObjectAnimator.ofFloat(linearLayoutProfileStatus, "translationY", 155f);
                    animation2.start();
                    profileStatusOn = true;

                }
            else{
                    LinearLayout linearLayoutProfileStatus = findViewById(R.id.LinearLayoutProfileStatus);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(linearLayoutProfileStatus, "translationX", 330f);
                    linearLayoutProfileStatus.setVisibility(View.VISIBLE);
                    animation.start();
                    ObjectAnimator animation2 = ObjectAnimator.ofFloat(linearLayoutProfileStatus, "translationY", -115f);
                    animation2.start();
                    linearLayoutProfileStatus.setVisibility(View.GONE);
                    profileStatusOn = false;

                }
            }
            }
        return true;
    }



    }

  /*  @Override
    protected void onStop() {
        super.onStop();
        mAuth.signOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }
*/
