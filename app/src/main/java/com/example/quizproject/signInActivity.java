package com.example.quizproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class signInActivity extends AppCompatActivity implements View.OnClickListener {
    EditText Email,Password;
    TextView tvFails;
    Button btnMoveToSignUp,btnSubmit;
    private FirebaseAuth mAuth;
    Dialog loadingpage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Email=findViewById(R.id.emailSignIn);
        Password=findViewById(R.id.passwordSignIn);
        tvFails=findViewById(R.id.tvFails);
        btnMoveToSignUp=findViewById(R.id.btnMoveToSignUp);
        btnSubmit=findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        btnMoveToSignUp.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent it=new Intent(this,MainActivity.class);
            startActivity(it);
        }
    }


    @Override
    public void onClick(View v) {
        if (btnSubmit==v){
            String emailSignIn = Email.getText().toString();
            String passwordSignIn = Password.getText().toString();
            if (emailSignIn.equals("") || passwordSignIn.equals("")) {
                tvFails.setText("you have to enter all data");
            }
            else{
                loadingpage=new Dialog(this);
                loadingpage.setContentView(R.layout.loading_layout);
                loadingpage.setCancelable(false);
                loadingpage.show();
                mAuth.signInWithEmailAndPassword(emailSignIn, passwordSignIn)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(signInActivity.this, "התחברת בהצלחה",Toast.LENGTH_SHORT).show();
                                    loadingpage.cancel();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent it=new Intent(signInActivity.this,MainActivity.class);
                                    startActivity(it);
                                } else {
                                    loadingpage.cancel();
                                    Toast.makeText(signInActivity.this, "ההתחברות נכשלה נסה שוב", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }

        }
        else if (btnMoveToSignUp==v){
            Intent it=new Intent(signInActivity.this,signUpActivity.class);
            startActivity(it);
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
