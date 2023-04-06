package com.example.quizproject;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user;
    boolean profileStatusOn=false;
    SharedPreferences.Editor edit;
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        sp = getSharedPreferences("info", 0);
        if (settings.getBoolean("my_first_time", true)) {
            sp = getSharedPreferences("info", 0);
            edit = sp.edit();
            edit.putString("emails", "");
            edit.putString("points", "");
            settings.edit().putBoolean("my_first_time", false).commit();
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        {
            if (this.getClass()==MainActivity.class) {
                if (mAuth.getCurrentUser() != null) {
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
            }
            else if (this.getClass()== PokerGameFindTable.class) {
                if (mAuth.getCurrentUser() != null) {
                    menu.findItem(R.id.mainPage).setVisible(true);
                    menu.findItem(R.id.leaderboard).setVisible(true);
                    menu.findItem(R.id.disconnect).setVisible(true);
                    menu.findItem(R.id.signInPage).setVisible(false);
                    menu.findItem(R.id.signUpPage).setVisible(false);
                    menu.findItem(R.id.profileStatus).setVisible(false);
                    menu.findItem(R.id.aboutPage).setVisible(true);

                }
            }
            else if(this.getClass()== SignInActivity.class) {
                menu.findItem(R.id.mainPage).setVisible(true);
                menu.findItem(R.id.leaderboard).setVisible(false);
                menu.findItem(R.id.disconnect).setVisible(false);
                menu.findItem(R.id.signInPage).setVisible(false);
                menu.findItem(R.id.signUpPage).setVisible(false);
                menu.findItem(R.id.profileStatus).setVisible(false);
                menu.findItem(R.id.aboutPage).setVisible(true);
            }
            else if(this.getClass()== SignUpActivity.class) {
                menu.findItem(R.id.mainPage).setVisible(true);
                menu.findItem(R.id.leaderboard).setVisible(false);
                menu.findItem(R.id.disconnect).setVisible(false);
                menu.findItem(R.id.signInPage).setVisible(false);
                menu.findItem(R.id.signUpPage).setVisible(false);
                menu.findItem(R.id.profileStatus).setVisible(false);
                menu.findItem(R.id.aboutPage).setVisible(true);
            }
            else if(this.getClass()== WinnersActivity.class){
                if (mAuth.getCurrentUser() != null) {
                    menu.findItem(R.id.mainPage).setVisible(true);
                    menu.findItem(R.id.leaderboard).setVisible(false);
                    menu.findItem(R.id.disconnect).setVisible(true);
                    menu.findItem(R.id.signInPage).setVisible(false);
                    menu.findItem(R.id.signUpPage).setVisible(false);
                    menu.findItem(R.id.profileStatus).setVisible(true);
                } else {
                    menu.findItem(R.id.mainPage).setVisible(true);
                    menu.findItem(R.id.leaderboard).setVisible(false);
                    menu.findItem(R.id.disconnect).setVisible(false);
                    menu.findItem(R.id.signInPage).setVisible(true);
                    menu.findItem(R.id.signUpPage).setVisible(true);
                    menu.findItem(R.id.profileStatus).setVisible(false);
                }
            }
        }
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Context context=this.getApplicationContext();
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
            if (this.getClass() == WinnersActivity.class) {
                if (mAuth.getCurrentUser() != null) {
                    String emails = sp.getString("emails", "");
                    String points = sp.getString("points", "");
                    String[] emailsArr = emails.split("#");
                    String[] pointsArr = points.split("#");
                    int index = Arrays.asList(emailsArr).indexOf("" + mAuth.getCurrentUser().getEmail());
                    if (index != -1) {
                        if (Integer.parseInt(pointsArr[index]) != -1) {
                            Toast.makeText(this, "" + mAuth.getCurrentUser().getEmail() + " have " + pointsArr[index] + " points", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "" + mAuth.getCurrentUser().getEmail() + "have no points yet", Toast.LENGTH_SHORT).show();
                        }
                    } else {//אם עשינו התחברות למשתמש שלא נוצר כאן אז ניקח את הפרטים שלו מהUSERS
                        FirebaseFirestore db;
                        Dialog loadingpage = new Dialog(this);
                        loadingpage.setContentView(R.layout.loading_layout);
                        loadingpage.setCancelable(false);
                        loadingpage.show();
                        db = FirebaseFirestore.getInstance();
                        DocumentReference docRef1 = db.collection("Users").document("usersDetail");
                        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {//פותח את המסמך של המשתמשים
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        if (document.getData().get(mAuth.getCurrentUser().getEmail()) != null) {
                                            Map<String, Object> documentUserHash = new HashMap<>();
                                            documentUserHash = document.getData();
                                            Map<String, Object> UserHash = (Map) documentUserHash.get(mAuth.getCurrentUser().getEmail());
                                            if (Integer.parseInt("" + UserHash.get("points")) != -1) {
                                                Toast.makeText(context, "" + mAuth.getCurrentUser().getEmail() + " have " + pointsArr[index] + " points", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "" + mAuth.getCurrentUser().getEmail() + " have no points yet", Toast.LENGTH_SHORT).show();
                                            }
                                            loadingpage.cancel();
                                            //תוך כדי ברקע אז נוסיף את המשתמש לSHERED כדי שפעם הבאה יהיה יותר קל לגשת
                                            //  תזכורת כדי להוסיף לSHERED צריך את הכל גם אימייל וגם נקודות
                                            String emails = sp.getString("emails", "");//מוסיף את האימייל והנקודות לshered כי התמונה נוספה כבר
                                            String points = sp.getString("points", "");
                                            emails = emails + mAuth.getCurrentUser().getEmail() + "#";
                                            points = points + UserHash.get("points") + "#";
                                            edit = sp.edit();
                                            edit.putString("emails", emails);
                                            edit.putString("points", points);
                                            edit.commit();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
            else {
                if (mAuth.getCurrentUser() != null) {
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
                        if (index != -1) {
                            if (Integer.parseInt(pointsArr[index]) != -1) {
                                pointsProfileStatus.setText("" + pointsArr[index]);
                            } else {
                                pointsProfileStatus.setText("no points yet");
                            }
                        } else {//אם עשינו התחברות למשתמש שלא נוצר כאן אז ניקח את הפרטים שלו מהUSERS
                            FirebaseFirestore db;
                            Dialog loadingpage = new Dialog(this);
                            loadingpage.setContentView(R.layout.loading_layout);
                            loadingpage.setCancelable(false);
                            loadingpage.show();
                            db = FirebaseFirestore.getInstance();
                            DocumentReference docRef1 = db.collection("Users").document("usersDetail");
                            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {//פותח את המסמך של המשתמשים
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            if (document.getData().get(user.getEmail()) != null) {
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
                                                String emails = sp.getString("emails", "");//מוסיף את האימייל והנקודות לshered כי התמונה נוספה כבר
                                                String points = sp.getString("points", "");
                                                emails = emails + user.getEmail() + "#";
                                                points = points + UserHash.get("points") + "#";
                                                edit = sp.edit();
                                                edit.putString("emails", emails);
                                                edit.putString("points", points);
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

                        profileStatusOn = true;
                    } else {
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
        }
        return true;
    }

}

