package com.example.quizproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    TextView questionsTv,timeTv,tvPoints;
    ImageButton btnAnswer1,btnAnswer2,btnAnswer3,btnAnswer4,btnStartQuiz;
    Button btnBackToQuestion,btnUseTheHelpPhone;
    boolean gameStart=false,answerAlready=false,wasOnStreak=false;
    int numOfHearts=3;
    int sumOfPoints=0;
    int numOfQuestionsAsked=0;
    int streakMode=0;
    CountDownTimer questionTimer,coolDown;
    Question question1,question2,question3,question4,question5,question6,question7,question8,question9,question10;
    Question[] questionsArr;
    int indexOfCurrentQuestion;
    ImageView heartImage1,heartImage2,heartImage3;
    LinearLayout LinearLayoutHearts;
    ConstraintLayout MainLinearLayout;
    ImageButton helpFifty,helpPhoneFriend;
    Dialog phoneDialog,loadingpage;
    int miliSecondsRemainingToStartAgain;
    private FirebaseAuth mAuth;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    boolean useButtonPhone;
    TextView tvanswers1,tvanswers2,tvanswers3,tvanswers4;
    CardView CardStartQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        //sharedpref
        sp=getSharedPreferences("info",0);
        edit=sp.edit();

        MainLinearLayout=findViewById(R.id.MainLinearLayout);
        LinearLayoutHearts=findViewById(R.id.LinearLayoutHearts);
        heartImage1=findViewById(R.id.heartImage1);
        heartImage2=findViewById(R.id.heartImage2);
        heartImage3=findViewById(R.id.heartImage3);
        btnAnswer1=findViewById(R.id.btnAnswer1);
        btnAnswer2=findViewById(R.id.btnAnswer2);
        btnAnswer3=findViewById(R.id.btnAnswer3);
        btnAnswer4=findViewById(R.id.btnAnswer4);
        btnStartQuiz=findViewById(R.id.btnStartQuiz);
        timeTv =findViewById(R.id.timeTv);
        questionsTv=findViewById(R.id.questionsTv);
        tvPoints=findViewById(R.id.tvPoints);
        helpPhoneFriend=findViewById(R.id.helpPhoneFriend);
        helpFifty=findViewById(R.id.helpFifty);
        loadingpage=new Dialog(this);
        loadingpage.setContentView(R.layout.loading_layout);
        loadingpage.setCancelable(false);
        mAuth=FirebaseAuth.getInstance();
        MainLinearLayout.getBackground().setAlpha(100);
        btnStartQuiz.setOnClickListener(this);
        btnAnswer1.setOnClickListener(this);
        btnAnswer2.setOnClickListener(this);
        btnAnswer3.setOnClickListener(this);
        btnAnswer4.setOnClickListener(this);
        helpPhoneFriend.setOnClickListener(this);
        helpFifty.setOnClickListener(this);
        question1= new Question(getString(R.string.question1), new String[]{getString(R.string.question1Ans1), getString(R.string.question1Ans2), getString(R.string.question1Ans3), getString(R.string.question1Ans4)},2,false);
        question2= new Question(getString(R.string.question2), new String[]{getString(R.string.question2Ans1), getString(R.string.question2Ans2), getString(R.string.question2Ans3), getString(R.string.question2Ans4)},1,false);
        question3= new Question(getString(R.string.question3), new String[]{getString(R.string.question3Ans1), getString(R.string.question3Ans2), getString(R.string.question3Ans3), getString(R.string.question3Ans4)},2,false);
        question4= new Question(getString(R.string.question4), new String[]{getString(R.string.question4Ans1), getString(R.string.question4Ans2), getString(R.string.question4Ans3), getString(R.string.question4Ans4)},4,false);
        question5= new Question(getString(R.string.question5), new String[]{getString(R.string.question5Ans1), getString(R.string.question5Ans2), getString(R.string.question5Ans3), getString(R.string.question5Ans4)},2,false);
        question6= new Question(getString(R.string.question6), new String[]{getString(R.string.question6Ans1), getString(R.string.question6Ans2), getString(R.string.question6Ans3), getString(R.string.question6Ans4)},4,false);
        question7= new Question(getString(R.string.question7), new String[]{getString(R.string.question7Ans1), getString(R.string.question7Ans2), getString(R.string.question7Ans3), getString(R.string.question7Ans4)},1,false);
        question8= new Question(getString(R.string.question8), new String[]{getString(R.string.question8Ans1), getString(R.string.question8Ans2), getString(R.string.question8Ans3), getString(R.string.question8Ans4)},3,false);
        question9= new Question(getString(R.string.question9), new String[]{getString(R.string.question9Ans1), getString(R.string.question9Ans2), getString(R.string.question9Ans3), getString(R.string.question9Ans4)},2,false);
        question10= new Question(getString(R.string.question10), new String[]{getString(R.string.question10Ans1), getString(R.string.question10Ans2), getString(R.string.question10Ans3), getString(R.string.question10Ans4)},3,false);
        questionsArr=new Question[]{question1,question2,question3,question4,question5,question6,question7,question8,question9,question10};
        tvanswers1=findViewById(R.id.tvanswers1);
        tvanswers2=findViewById(R.id.tvanswers2);
        tvanswers3=findViewById(R.id.tvanswers3);
        tvanswers4=findViewById(R.id.tvanswers4);
        CardStartQuiz=findViewById(R.id.CardStartQuiz);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent it=new Intent(this,MainActivity.class);
            startActivity(it);
        }
        coolDown = new CountDownTimer(1500, 1000) {
            public void onTick(long millisUntilFinished) {
                btnAnswer1.setAlpha(1);
                btnAnswer2.setAlpha(1);
                btnAnswer3.setAlpha(1);
                btnAnswer4.setAlpha(1);
                btnAnswer1.setClickable(false);
                btnAnswer2.setClickable(false);
                btnAnswer3.setClickable(false);
                btnAnswer4.setClickable(false);
            }

            public void onFinish() {
                btnAnswer1.setClickable(true);
                btnAnswer2.setClickable(true);
                btnAnswer3.setClickable(true);
                btnAnswer4.setClickable(true);
                startQuiz();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (btnStartQuiz == v) {
            btnStartQuiz.setClickable(false);
            btnStartQuiz.setVisibility(View.GONE);
            CardStartQuiz.setVisibility(View.GONE);
            createHeartsAndPoints();
            new CountDownTimer(3000, 1) {

                public void onTick(long millisUntilFinished) {
                    timeTv.setText("החידון מתחיל בעוד: " + "\n" + (new SimpleDateFormat("mm:ss:SS").format(new Date(millisUntilFinished))));
                }

                public void onFinish() {
                    gameStart = true;
                    startQuiz();
                }
            }.start();
        }
        else if (gameStart) {
            useButtonPhone=false;
            if (helpFifty == v) {
                boolean checkRandom=true;
                while (checkRandom) {
                    int randomNum1 = (int) (Math.random() * 4);
                    int randomNum2 = (int) (Math.random() * 4);
                    if ((questionsArr[indexOfCurrentQuestion].getNumOfCorrectAnswer() != randomNum1 )&&(questionsArr[indexOfCurrentQuestion].getNumOfCorrectAnswer() != randomNum2)&&(randomNum1!= randomNum2)) {
                        if (randomNum1 == 0 || randomNum2==0) {
                            btnAnswer1.setAlpha(0.5F);
                            btnAnswer1.setClickable(false);
                        }
                        if (randomNum1 == 1 || randomNum2==1) {
                            btnAnswer2.setAlpha(0.5F);
                            btnAnswer2.setClickable(false);
                        }
                        if (randomNum1 == 2 || randomNum2==2) {
                            btnAnswer3.setAlpha(0.5F);
                            btnAnswer3.setClickable(false);
                        }
                        if (randomNum1==3 || randomNum2==3){
                            btnAnswer4.setAlpha(0.5F);
                            btnAnswer4.setClickable(false);
                        }
                        checkRandom=false;
                    }
                }
                helpFifty.setClickable(false);
                helpFifty.setAlpha(0.4F);
            }
            else if (helpPhoneFriend == v) {
                String time = timeTv.getText().toString();
                String[] timeHelpArr = time.split("\n");
                String[] timeArr = timeHelpArr[1].split(":");
                miliSecondsRemainingToStartAgain = Integer.parseInt(timeArr[0]) * 60000 + Integer.parseInt(timeArr[1]) * 1000;
                questionTimer.cancel();
                phoneDialog = new Dialog(this);
                phoneDialog.setContentView(R.layout.phonedialog);
                phoneDialog.setCancelable(false);
                btnUseTheHelpPhone = phoneDialog.findViewById(R.id.btnUseTheHelpPhone);
                btnBackToQuestion = phoneDialog.findViewById(R.id.btnBackToQuestion);
                btnUseTheHelpPhone.setOnClickListener(this);
                btnBackToQuestion.setOnClickListener(this);
                phoneDialog.show();
            }
            else if (btnBackToQuestion == v) {
                phoneDialog.cancel();
                questionTimer=new CountDownTimer(miliSecondsRemainingToStartAgain, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timeTv.setText("זמן שנותר לשאלה:" + "\n" + (new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished))));
                    }
                    public void onFinish() {
                        endQuestion(false, 0,0);
                    }
                }.start();
            }
            else if (btnUseTheHelpPhone == v) {
                Intent itPhone = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:0545957055"));
                phoneDialog.cancel();
                helpPhoneFriend.setAlpha(0.4F);
                useButtonPhone=true;
                helpPhoneFriend.setClickable(false);
                startActivity(itPhone);
            }
            else {
                String time = timeTv.getText().toString();
                String[] timeHelpArr = time.split("\n");
                String[] timeArr = timeHelpArr[1].split(":");
                int sumOfTime = Integer.parseInt(timeArr[0]) * 60000 + Integer.parseInt(timeArr[1]) * 1000;
                if (btnAnswer1 == v) {
                    answerAlready = true;
                    endQuestion(true, 1, sumOfTime);
                } else if (btnAnswer2 == v) {
                    answerAlready = true;
                    endQuestion(true, 2, sumOfTime);
                } else if (btnAnswer3 == v) {
                    answerAlready = true;
                    endQuestion(true, 3, sumOfTime);
                } else if (btnAnswer4 == v) {
                    answerAlready = true;
                    endQuestion(true, 4, sumOfTime);
                }
            }
        }
    }

    public void createHeartsAndPoints(){
        LinearLayoutHearts.setVisibility(View.VISIBLE);
        tvPoints.setVisibility(View.VISIBLE);
        Animation fadeIn= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        heartImage1.setAnimation(fadeIn);
        heartImage2.setAnimation(fadeIn);
        heartImage3.setAnimation(fadeIn);
    }

    public void changeHearts(){
        if (numOfHearts==2){
            heartImage1.animate()
                    .alpha(0f)
                    .setDuration(1500)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            heartImage1.setVisibility(View.GONE);
                        }
                    });
        }
        else if (numOfHearts==1){
            heartImage2.animate()
                    .alpha(0f)
                    .setDuration(1500)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            heartImage2.setVisibility(View.GONE);
                        }
                    });
        }
        else if (numOfHearts==0){
            heartImage3.animate()
                    .alpha(0f)
                    .setDuration(1500)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            heartImage3.setVisibility(View.GONE);
                        }
                    });
        }


    }

    public void startQuiz(){
        if (numOfQuestionsAsked!=questionsArr.length) {
            indexOfCurrentQuestion= searchNewQuestion();
            questionsArr[indexOfCurrentQuestion].setIsUsed(true);
            numOfQuestionsAsked++;
            questionsArr[indexOfCurrentQuestion].shuffle();
            questionsTv.setText("" + questionsArr[indexOfCurrentQuestion].getQuestion()+"?");
            tvanswers1.setText("" + questionsArr[indexOfCurrentQuestion].getAnswersArr()[0]);
            tvanswers2.setText("" + questionsArr[indexOfCurrentQuestion].getAnswersArr()[1]);
            tvanswers3.setText("" + questionsArr[indexOfCurrentQuestion].getAnswersArr()[2]);
            tvanswers4.setText("" + questionsArr[indexOfCurrentQuestion].getAnswersArr()[3]);
            questionTimer =new CountDownTimer(30600, 1000) {//TODO  ולתקן את העזרת חבר
                public void onTick(long millisUntilFinished) {
                    timeTv.setText("זמן שנותר לשאלה:" + "\n" + (new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished))));
                }
                public void onFinish() {
                    coolDown.start();
                    endQuestion(false, 0,0);
                }
            }.start();

        }
        else{
            endGame();
        }

    }

    public int searchNewQuestion(){
        Random rand = new Random();
        int randomNum = rand. nextInt(10);
        boolean found=false;
        while (!found){
            if (questionsArr[randomNum].getIsUsed()) {
                randomNum = rand. nextInt(10);
            }
            else{
                return randomNum;
            }
        }
        return -1;
    }

    public void endQuestion(boolean isClick,int numOfAnswer,int answerTime){
        questionTimer.cancel();
        markTheRightAnswer(questionsArr[indexOfCurrentQuestion].getNumOfCorrectAnswer());
        if (isClick){
            if (questionsArr[indexOfCurrentQuestion].getNumOfCorrectAnswer()==(numOfAnswer-1)){
                streakMode++;
                double numPoints = answerTime * 0.00169977;
                if(streakMode>2){
                    wasOnStreak=true;
                    if (streakMode==3 || streakMode==4){
                        numPoints=numPoints*1.2;
                        sumOfPoints+=(int) (numPoints);
                        createToast(false, (int) numPoints, true,1);
                        tvPoints.setText("הנקודות שצברת: " + sumOfPoints);
                    }
                    else if (streakMode==5 || streakMode==6){
                        numPoints=numPoints*1.4;
                        sumOfPoints+=(int) (numPoints);
                        createToast(false, (int) numPoints, true,1);
                        tvPoints.setText("הנקודות שצברת: " + sumOfPoints);
                    }
                    else if (streakMode==7 || streakMode==8){
                        numPoints=numPoints*1.6;
                        sumOfPoints+=(int) (numPoints);
                        createToast(false, (int) numPoints, true,1);
                        tvPoints.setText("הנקודות שצברת: " + sumOfPoints);
                    }
                    else if (streakMode==9 || streakMode==10){
                        numPoints=numPoints*1.8;
                        sumOfPoints+=(int) (numPoints);
                        createToast(false, (int) numPoints, true,1);
                        tvPoints.setText("הנקודות שצברת: " + sumOfPoints);
                    }
                    else{
                        numPoints=numPoints*2;
                        sumOfPoints+=(int) (numPoints);
                        createToast(false, (int) numPoints, true,1);
                        tvPoints.setText("הנקודות שצברת: " + sumOfPoints);
                    }
                }
                else{
                    sumOfPoints+=(int) (numPoints);
                    createToast(false, (int) numPoints, true,-1);
                    tvPoints.setText("הנקודות שצברת: " + sumOfPoints);
                }
                coolDown.start();
            }
            else{
                numOfHearts=numOfHearts-1;
                changeHearts();
                if (numOfHearts==0){
                    endGame();
                }
                else {
                    if (streakMode>2) {
                        createToast(true, 0, true,0);
                   //     disableStreak();
                    }
                    else{
                        createToast(true, 0, true,-1);
                    }
                    coolDown.start();
                }
                streakMode=0;
            }
        }
        else{
            numOfHearts=numOfHearts-1;
            streakMode=0;
            changeHearts();
            if (numOfHearts==0){
                endGame();
            }
            else {
                if (streakMode>2) {
                    createToast(true, 0, false,0);
                   //disableStreak();
                }
                else{
                    createToast(true, 0, false,-1);
                }
                coolDown.start();
            }
        }

    }

    public void markTheRightAnswer(int numOfRightAnswer){
        if (numOfRightAnswer==0){
            new CountDownTimer(1000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.rightAnswerColor));
                }
                @Override
                public void onFinish() {
                    btnAnswer1.setBackground(getDrawable(R.drawable.button_round_corners));
                }
            }.start();
        }
        else if (numOfRightAnswer==1){
            new CountDownTimer(1000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.rightAnswerColor));
                }
                @Override
                public void onFinish() {
                    btnAnswer2.setBackground(getDrawable(R.drawable.button_round_corners));
                }
            }.start();
        }
        else if (numOfRightAnswer==2){
            new CountDownTimer(1000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.rightAnswerColor));
                }
                @Override
                public void onFinish() {
                    btnAnswer3.setBackground(getDrawable(R.drawable.button_round_corners));
                }
            }.start();
        }
        else if (numOfRightAnswer==3){
            new CountDownTimer(1000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.rightAnswerColor));
                }
                @Override
                public void onFinish() {
                    btnAnswer4.setBackground(getDrawable(R.drawable.button_round_corners));
                }
            }.start();
        }
    }
    public void endGame(){
        questionTimer.cancel();
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        FirebaseUser currentUser =mAuth.getCurrentUser();
        loadingpage.show();
        //אני פותח את הדף ולוקח את הנקודות של המשתמש הנוכחי ובודק אם מה שעשה עכשיו יותר טוב אם לא  אז משאיר ככה אם כן אז מחליף את כל הטופס עם overwrite של המסמך ממקודם פלוס המשתמש הנוכחי עם הנקודות העדכני
        DocumentReference docRef = db.collection("Users").document("usersDetail");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getData().get(currentUser.getEmail())!=null) {
                            Map<String, Object> documentUserHash = new HashMap<>();
                            documentUserHash = document.getData();
                            Map<String, Object> UserHash = (Map) documentUserHash.get(mAuth.getCurrentUser().getEmail());
                            int numHighestPoint=Integer.parseInt(""+UserHash.get("points"));
                            if (sumOfPoints>numHighestPoint){
                                User newUser=new User ((""+UserHash.get("userName")),(""+UserHash.get("email")));
                                newUser.setPoints(sumOfPoints);
                                newUser.setProfileImageUri(mAuth.getCurrentUser().getPhotoUrl());
                                documentUserHash.put(currentUser.getEmail(),newUser);

                                db.collection("Users").document("usersDetail")//משנה במסמך של המשתמשים את הנקודות העדכניות
                                        .set(documentUserHash)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(QuizActivity.this," כל הכבוד השגת "+sumOfPoints+" נקודות ",Toast.LENGTH_SHORT).show();
                                                Intent it=new Intent(QuizActivity.this, WinnersActivity.class);
                                                it.putExtra("points",sumOfPoints);//מעביר לעמוד הבא את המספר נקודות החדש
                                                String emails=sp.getString("emails","");// משנה בsered pref  את הנקודות העדכניות של המשתמש
                                                String points=sp.getString("points","");
                                                String[] emailsArr = emails.split("#");
                                                String[] pointsArr = points.split("#");
                                                int index = Arrays.asList(emailsArr).indexOf("" + mAuth.getCurrentUser().getEmail());
                                                pointsArr[index]=""+sumOfPoints;//משנה את המקום של המשתמש ב shered ושם את המספר נקודות החדש
                                                String pointsNew="";
                                                for (int i = 0; i < emailsArr.length; i++) {//רץ על כל המערך נקודות החדש וממיר אותו לסטרינג עם # בין לבין
                                                    pointsNew=pointsNew+pointsArr[i]+"#";
                                                }
                                                edit.putString("points",pointsNew);
                                                edit.commit();
                                                loadingpage.cancel();
                                                startActivity(it);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(QuizActivity.this,"אוי חבל לא עבד",Toast.LENGTH_SHORT).show();
                                                loadingpage.cancel();
                                            }
                                        });


                            }
                            else {
                                Toast.makeText(QuizActivity.this, " כל הכבוד השגת " + sumOfPoints + " נקודות היו לך פעמים יותר טובות", Toast.LENGTH_SHORT).show();
                                Intent it=new Intent(QuizActivity.this, WinnersActivity.class);//לא משנה כלום ב SHERED כי כבר מופיע התוצאה הכי טובה שלו
                                it.putExtra("points",-1);//מעביר לעמוד הבא את המספר -1 כדי שידע שלא שיפר תוצאה
                                startActivity(it);
                            }
                        }
                    } else {
                        Toast.makeText(QuizActivity.this, "No such document",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QuizActivity.this, "get failed with ",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void createToast(Boolean isFail,int numPoints,boolean finishQuestion,int onStreak){//onStreak - 1=on streak , 0 was on streak and just get wrong, -1 never on streak
        LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.layout_toast));
        LinearLayout.LayoutParams LL1=new LinearLayout.LayoutParams(300,150);
        toastLayout.setLayoutParams(LL1);
        TextView toastText = toastLayout.findViewById(R.id.tvToast);
        TextView tvStreak =toastLayout.findViewById(R.id.tvToastStreak);
        ImageView toastImage = toastLayout.findViewById(R.id.ivToast);

        if (isFail) {
            if (finishQuestion) {
                if (onStreak==0){
                    toastText.setText("טעית נשאר לך " + numOfHearts + " לבבות");
                    tvStreak.setText("הרצף הופסק לא נורא נסה להשיגו שוב");
                    tvStreak.setBackgroundResource(0);
                    tvStreak.setPadding(120,30,120,30);
                    tvStreak.setVisibility(View.VISIBLE);
                }
                else {
                    toastText.setText("טעית נשאר לך " + numOfHearts + " לבבות");
                    tvStreak.setVisibility(View.GONE);
                }
                toastLayout.setBackgroundColor(getResources().getColor(R.color.wrongAnswerColor));
                toastImage.setImageResource(R.drawable.wrongimage);
            }
            else{
                if (onStreak==0){
                    toastText.setText("לא הספקת לענות על השאלה נשאר לך " + numOfHearts + " לבבות");
                    tvStreak.setText("הרצף הופסק לא נורא נסה להשיגו שוב");
                    tvStreak.setBackgroundResource(0);
                    tvStreak.setPadding(120,30,120,30);
                    tvStreak.setVisibility(View.VISIBLE);
                }
                else {
                    toastText.setText("לא הספקת לענות על השאלה נשאר לך " + numOfHearts + " לבבות");
                    tvStreak.setVisibility(View.GONE);
                }
                toastImage.setImageResource(R.drawable.notontimeimage);
                toastLayout.setBackgroundColor(getResources().getColor(R.color.noTimeAnswerColor));
                startQuiz();
            }
        }
        else{
            if (onStreak>0) {
                tvStreak.setVisibility(View.VISIBLE);
                toastText.setText("צדקת קיבלת עוד " + numPoints + " נקודות");
                tvStreak.setText("כל הכבוד אתה ברצף של "+streakMode+" המשך כך");
                tvStreak.setBackground(getDrawable(R.drawable.fireborder));
                tvStreak.setPadding(130,50,130,50);
                toastLayout.setBackgroundColor(getResources().getColor(R.color.rightAnswerColor));
                toastImage.setImageResource(R.drawable.rightimage);
            }
            else{
                toastText.setText("צדקת קיבלת עוד " + numPoints + " נקודות");
                toastLayout.setBackgroundColor(getResources().getColor(R.color.rightAnswerColor));
                toastImage.setImageResource(R.drawable.rightimage);
            }
        }

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (numOfHearts!=0 || numOfQuestionsAsked!=10|| !useButtonPhone ) {
            questionTimer.cancel();
            coolDown.cancel();

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(useButtonPhone){
            questionTimer.start();
            Toast.makeText(this, "continue the game", Toast.LENGTH_SHORT).show();
        }

    }
}
