package com.example.quizproject;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    TextView tvExplainQuiz, nameAndChooseQuiz;
    Dialog explainQuizDialog;
    ImageButton foodQuizImageButton,pokerGameImageButton;
    Button btnFinishDialog, btnBackDialog;
    ImageButton btnDisconnect;
    ImageButton btnSignUp,btnSignIn;
    String chosenQuizName;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FrameLayout disconnectFrameLayout;
    SharedPreferences sp;
    boolean profileStatusOn=false;
    SharedPreferences.Editor edit;
    FirebaseFirestore db;
    int num=0;
    LinearLayout linearLayout;




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
        pokerGameImageButton.setBackgroundResource(R.drawable.pokerimagetomain);
        //
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        nameAndChooseQuiz = findViewById(R.id.nameAndChooseQuiz);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(this);
        linearLayout=findViewById(R.id.linearLayout);
        disconnectFrameLayout=findViewById(R.id.disconnectFrameLayout);
        user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            nameAndChooseQuiz.setTextSize(30);
            if (name.matches("[a-zA-Z0-9]+")) {
                nameAndChooseQuiz.setText("Hey " + name + " choose a game");
            } else {
                nameAndChooseQuiz.setText("היי " + name + " בחר משחק");

            }
            disconnectFrameLayout.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            ///////create listener to notifications

        } else {
            disconnectFrameLayout.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
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
            tvExplainQuiz.setText("חידון הפוקר מתבצע כך שהשחקן מתחיל עם 3 לבבות לאחר שהשחקן טעה בשלושה שאלות המשחק נגמר. בכל שאלה יש אפשרות לזכות במספר נקודות שונה, ככל שהתשובה נענת יותר מהר כך מספר הנקודות על השאלה עולה בסוף המשחק יופיע מספר הנקודות שצבר השחקן ומה שיא נקודותיו בחידון הפוקר");
            btnFinishDialog.setOnClickListener(this);
            btnBackDialog.setOnClickListener(this);
            explainQuizDialog.show();
        } else if (btnSignIn == v) {
            if (user == null) {
                Intent it;
                it = new Intent(this, SignInActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(this, " אתה מחובר כבר" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        } else if (btnSignUp == v) {
            if (user == null) {
                Intent it;
                it = new Intent(this, SignUpActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(this, " אתה מחובר כבר" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        } else if (btnBackDialog == v) {
            explainQuizDialog.dismiss();
        } else if (v == btnFinishDialog) {
            Intent it = new Intent();
            if (chosenQuizName.equals("food")) {
                it = new Intent(this, QuizActivity.class);
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
            it = new Intent(this, PokerGameFindTable.class);
            startActivity(it);


        }
    }


/*
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

 */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.signInPage) {
            intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        } else if (id == R.id.signUpPage) {
            intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.leaderboard) {
            intent = new Intent(this, WinnersActivity.class);
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
*/




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
