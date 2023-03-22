package com.example.quizproject;

import static android.app.PendingIntent.getActivity;

import static java.lang.Thread.sleep;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class pokerGameActivity extends AppCompatActivity implements View.OnClickListener {

    Intent it;
    StorageReference storageRef;
    FirebaseStorage storage;
    HashMap<String, Object> resultPokerTableGamesToFirstDeal;//משמש לסדר בהתחלה את מערך הUSERS
    ArrayList<User> usersArrListInGame=new ArrayList<User>();
    int tableNum,numOfPlayers;
    int numStart,myPlace;
    Dialog loadingpage;
    Map<String, Object> documentUsersInFireBase;
    DatabaseReference dataBaseRef;
    private FirebaseAuth mAuth;
    int yourMoney,numInPot=0;
    int[] numOthersInTable;//עשיתי ARR שמראה לפי המקום שמכניסים נגיד איש 2 במקום ARR[1] ויתן את המקום
    /////set cards
    ValueEventListener listener;
    boolean alreadyGetInChangedCards=false; //עזר ל LISTENER TO ROUND שלא יכנס לפעולה מלא פעמים
    ////////////
    ///
    ArrayList<Card> myHand=new ArrayList<Card>();
    CountDownTimer countDownTimer;
    ValueEventListener listenerToChangeTurn,listenerToChangeRound,listenerToEndGame;


    /////
    LinearLayout LinearLayoutToRaiseButton;
    TextView player4TextViewName,player4TextViewMoney,player3TextViewMoney,player3TextViewName,player2TextViewName,player2TextViewMoney,player1TextViewName,player1TextViewMoney;
    ImageView player4ProfileImage,player2ProfileImage,player1ProfileImage,yourturniconplayer1,yourturniconplayer2,yourturniconplayer3,yourturniconplayer4,player3ProfileImage;
    TextView textViewTimer;
    ConstraintLayout player4constraintlayout,player3constraintlayout,player2constraintlayout,player1constraintlayout;
    ImageButton foldbtnicon,callbtnicon,raisebtnIcon;
    TextView tvRaisedplayer1,tvRaisedplayer2,tvRaisedplayer3,tvRaisedplayer4;
    ImageView btnWhatDidPlayer1Icon,btnWhatDidPlayer2Icon,btnWhatDidPlayer3Icon,btnWhatDidPlayer4Icon;
    TextView tvRaise;
    SeekBar SeekbarRaise;
    ConstraintLayout constraintRaise;
    HashMap<String,Object>myTurnDownloadedAllDocument;
    boolean canDoOnlyAllIn=false;
    ImageView cardFlopNumber1,cardFlopNumber2,cardFlopNumber3,cardFlopNumber4,cardFlopNumber5;
    CardView cardFlopParent5,cardFlopParent4,cardFlopParent3,cardFlopParent2,cardFlopParent1;
    String Action="";
    int numOfTurnsIHaveInSameRound=0;
    int[] arrMoneyOfEveryPlayerInStartOfGame;
    ImageView myCardNumber1,myCardNumber2;
    int numRound=0;
    int numGamesPlayedCounter=1;
    boolean checkIfIEndRound=false;
    ValueEventListener ToastListener,listenerEndAllGame;
    ////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//עושה למשתמש בLANDSCAPE
        setContentView(R.layout.activity_poker_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {}
        ////////////
        {
            LinearLayoutToRaiseButton=findViewById(R.id.LinearLayoutToRaiseButton);
            LinearLayoutToRaiseButton.setVisibility(View.GONE);
            LinearLayoutToRaiseButton.setAlpha(0);
            LinearLayoutToRaiseButton.setOnClickListener(this);
            player4TextViewName = findViewById(R.id.player4TextViewName);
            player4TextViewMoney = findViewById(R.id.player4TextViewMoney);
            player3TextViewMoney = findViewById(R.id.player3TextViewMoney);
            player3TextViewName = findViewById(R.id.player3TextViewName);
            player2TextViewName = findViewById(R.id.player2TextViewName);
            player2TextViewMoney = findViewById(R.id.player2TextViewMoney);
            player1TextViewName = findViewById(R.id.player1TextViewName);
            player1TextViewMoney = findViewById(R.id.player1TextViewMoney);
            player4ProfileImage = findViewById(R.id.player4ProfileImage);
            player2ProfileImage = findViewById(R.id.player2ProfileImage);
            player1ProfileImage = findViewById(R.id.player1ProfileImage);
            player4constraintlayout = findViewById(R.id.player4constraintlayout);
            player3constraintlayout = findViewById(R.id.player3constraintlayout);
            player2constraintlayout = findViewById(R.id.player2constraintlayout);
            player1constraintlayout = findViewById(R.id.player1constraintlayout);
            yourturniconplayer1 = findViewById(R.id.yourturniconplayer1);
            yourturniconplayer2 = findViewById(R.id.yourturniconplayer2);
            yourturniconplayer3 = findViewById(R.id.yourturniconplayer3);
            yourturniconplayer4 = findViewById(R.id.yourturniconplayer4);
            tvRaisedplayer1 = findViewById(R.id.tvRaisedplayer1);
            tvRaisedplayer2 = findViewById(R.id.tvRaisedplayer2);
            tvRaisedplayer3 = findViewById(R.id.tvRaisedplayer3);
            tvRaisedplayer4 = findViewById(R.id.tvRaisedplayer4);
            tvRaise = findViewById(R.id.tvRaise);
            SeekbarRaise = findViewById(R.id.SeekbarRaise);
            btnWhatDidPlayer1Icon = findViewById(R.id.btnWhatDidPlayer1Icon);
            btnWhatDidPlayer2Icon = findViewById(R.id.btnWhatDidPlayer2Icon);
            btnWhatDidPlayer3Icon = findViewById(R.id.btnWhatDidPlayer3Icon);
            btnWhatDidPlayer4Icon = findViewById(R.id.btnWhatDidPlayer4Icon);
            foldbtnicon = findViewById(R.id.foldbtnicon);
            callbtnicon = findViewById(R.id.callbtnicon);
            raisebtnIcon = findViewById(R.id.raisebtnIcon);
            player3ProfileImage = findViewById(R.id.player3ProfileImage);
            foldbtnicon.setOnClickListener(this);
            raisebtnIcon.setOnClickListener(this);
            callbtnicon.setOnClickListener(this);
            textViewTimer = findViewById(R.id.textViewTimer);
            constraintRaise = findViewById(R.id.constraintRaise);
            myCardNumber1 = findViewById(R.id.myCardNumber1);
            myCardNumber2 = findViewById(R.id.myCardNumber2);
            cardFlopNumber1 = findViewById(R.id.cardFlopNumber1);
            cardFlopNumber2 = findViewById(R.id.cardFlopNumber2);
            cardFlopNumber3 = findViewById(R.id.cardFlopNumber3);
            cardFlopNumber4 = findViewById(R.id.cardFlopNumber4);
            cardFlopNumber5 = findViewById(R.id.cardFlopNumber5);
            cardFlopParent1 = findViewById(R.id.cardFlopParent1);
            cardFlopParent2 = findViewById(R.id.cardFlopParent2);
            cardFlopParent3 = findViewById(R.id.cardFlopParent3);
            cardFlopParent4 = findViewById(R.id.cardFlopParent4);
            cardFlopParent5 = findViewById(R.id.cardFlopParent5);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
        }
        dataBaseRef = FirebaseDatabase.getInstance(getString(R.string.fireBaseGetInstance)).getReference();
        ///////////////
        loadingpage = new Dialog(this);
        loadingpage.setContentView(R.layout.loading_layout);
        loadingpage.setCancelable(false);
        // loadingpage.show();
        mAuth = FirebaseAuth.getInstance();

        it = getIntent();
        tableNum = it.getIntExtra("tableNumber", 0);
        downoadrealtime();
        setListenerToEndAllGame();//שיהיה בינתיים
        toastToEveryOneListener();//שיהיה בינתיים

    }

    public void onClick(View view) {
        countDownTimer.cancel();
        boolean selectBtn=false;
        if (view==foldbtnicon){
            selectBtn=true;
            player1constraintlayout.setBackgroundColor(Color.RED);
            btnWhatDidPlayer1Icon.setImageDrawable(getDrawable(R.drawable.foldiconpoker));
            HashMap<String,Integer> usersDetails=(HashMap<String,Integer>)myTurnDownloadedAllDocument.get("usersDetails");
            HashMap<String,String> usersAction=(HashMap<String,String>)myTurnDownloadedAllDocument.get("activeActions");
            String[] actionOneBeforearr=usersAction.get(mAuth.getCurrentUser().getEmail().replace(".","*")).split(":");
            if (actionOneBeforearr.length==1){
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(mAuth.getCurrentUser().getEmail().replace(".","*")).setValue("fold");
                Action="fold";
                checkIfDoneRoundAndGame();//שולח לבדוקה ומשם לסיום התור
            }
            else {
                int numRaise = Integer.parseInt("" + actionOneBeforearr[1]);
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(mAuth.getCurrentUser().getEmail().replace(".","*")).setValue("fold");
                HashMap<String,Integer> tableHash=(HashMap<String,Integer>)myTurnDownloadedAllDocument.get("tableDetails");
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("Kupa").setValue((Integer.parseInt(""+tableHash.get("Kupa"))+numRaise));
                Action="fold:";
                checkIfDoneRoundAndGame();//שולח לבדוקה ומשם לסיום התור
            }
            //לכתוב לכולם שיצא ולבדוק אם לפי היה לו כסף שהוא שם
            //TODO לבדוק אם מישהו עשה FOLD לשים לו את הכסף שבDATABASE כי יש מצב שילם ואז פרש
        }
        else if (raisebtnIcon==view){//TODO לעשות בדיקה לפני התור שלי אם העלו והכל ולעשות גם קלפים
            if (constraintRaise.getVisibility()==View.GONE){
                LinearLayoutToRaiseButton.setVisibility(View.VISIBLE);
                constraintRaise.setVisibility(View.VISIBLE);
                player2constraintlayout.setVisibility(View.INVISIBLE);
                checkMaxMinToRaiseAndCall("raise");
            }
            else{
                if (tvRaise.getText()!=null && !tvRaise.getText().toString().equals("")){
                    player2constraintlayout.setVisibility(View.VISIBLE);
                    constraintRaise.setVisibility(View.GONE);
                    LinearLayoutToRaiseButton.setVisibility(View.GONE);
                    String Raise=tvRaise.getText().toString();
                    String[] RaiseArr=Raise.split("\\$");
                    int numRaise=Integer.parseInt(""+RaiseArr[0]);
                    int numMaxToRaise=SeekbarRaise.getMax();
                    if((numMaxToRaise*10)==numRaise){
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(mAuth.getCurrentUser().getEmail().replace(".", "*")).setValue("allIn:" + numRaise);
                        Action="allIn:"+numRaise;
                    }
                    else {
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(mAuth.getCurrentUser().getEmail().replace(".", "*")).setValue("raise:" + numRaise);
                        Action="raise:"+numRaise;
                    }
                    yourMoney=arrMoneyOfEveryPlayerInStartOfGame[myPlace]-numRaise;
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(mAuth.getCurrentUser().getEmail().replace(".","*")).setValue(yourMoney);
                    checkIfDoneRoundAndGame();//שולח לבדוקה ומשם לסיום התור

                }
                else{
                    LinearLayoutToRaiseButton.setVisibility(View.GONE);
                    constraintRaise.setVisibility(View.GONE);
                    player2constraintlayout.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (callbtnicon==view){
            selectBtn=true;
            int check=checkMaxMinToRaiseAndCall("call");
            if (check!=-1){//לא צריך ALLIN
                //מעדכן את המסמכים ועושה CALL מוריד מהכסף שלו ולא נוגע בקופה
                if (check!=yourMoney) { //אתה לא בALLIN
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(mAuth.getCurrentUser().getEmail().replace(".", "*")).setValue("call:" + check);
                    yourMoney = arrMoneyOfEveryPlayerInStartOfGame[myPlace] - check;
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(mAuth.getCurrentUser().getEmail().replace(".", "*")).setValue(yourMoney);
                    Action = "call:" + check;
                    checkIfDoneRoundAndGame();//שולח לבדוקה ומשם לסיום התור
                }
                else {
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(mAuth.getCurrentUser().getEmail().replace(".", "*")).setValue("allIn:" + check);
                    yourMoney = arrMoneyOfEveryPlayerInStartOfGame[myPlace] - check;
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(mAuth.getCurrentUser().getEmail().replace(".", "*")).setValue(yourMoney);
                    Action = "allIn:" + check;
                    checkIfDoneRoundAndGame();//שולח לבדוקה ומשם לסיום התור
                }
            }
        }
        else if (LinearLayoutToRaiseButton==view){
            constraintRaise.setVisibility(View.GONE);
            LinearLayoutToRaiseButton.setVisibility(View.GONE);
        }
    }

    //נסדר לפי סדר פעולות
////////before start game
    public void downoadrealtime(){
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//לקחנו את כל המסמך
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(pokerGameActivity.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        HashMap<String, Object> resultPokerTableGames = (HashMap<String, Object>) task.getResult().getValue();
                        HashMap<String,Integer> usersDetails=(HashMap<String,Integer>)resultPokerTableGames.get("usersDetails");
                        HashMap<String,Object> tableDetails= (HashMap<String,Object>)resultPokerTableGames.get("tableDetails");
                        numOfPlayers=usersDetails.size();
                        arrMoneyOfEveryPlayerInStartOfGame=new int[numOfPlayers];
                        ArrayList<String> emailsInGame=new ArrayList<String>();
                        int numToEach=0,i=0;
                        for(Map.Entry<String,Integer> entry : usersDetails.entrySet()) {
                            if(entry.getKey().replace("*",".").equals(mAuth.getCurrentUser().getEmail())){
                                myPlace=i;
                            }
                            emailsInGame.add(entry.getKey().replace(".","*"));
                            numToEach=Integer.parseInt(""+entry.getValue());
                            i++;
                        }
                        numStart=numToEach;
                        yourMoney=numStart;
                        openTableUserDetails(emailsInGame,numToEach);
                        numInPot=Integer.parseInt(""+tableDetails.get("numInPot"));
                    }
                    catch (Exception e){
                        Toast.makeText(pokerGameActivity.this,"eror",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }//מוריד קודם את הreal time קורא להוריד USERDETAILS
    public void openTableUserDetails(ArrayList<String> emailsInGame,int numToEach){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docRef1 = db.collection("Users").document("usersDetail");
        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {//פותח את המסמך של המשתמשים
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        documentUsersInFireBase = document.getData();
                        usersArrListInGame=new ArrayList<User>();
                        for (Map.Entry<String, Object> entry : documentUsersInFireBase.entrySet()) {
                            for (int i = 0; i < emailsInGame.size(); i++) {
                                if (emailsInGame.get(i).replace("*",".").equals(""+entry.getKey())) {
                                    HashMap<String, Object> map = (HashMap<String, Object>) entry.getValue();
                                    User user = new User("" + "" + map.get("userName"), "" + map.get("email"));
                                    user.setProfileImageUri(Uri.parse("" + map.get("profileImageUri")));
                                    user.setPoints(numToEach);
                                    usersArrListInGame.add(user);
                                }
                            }
                        }
                        setTableStartGame();
                    }
                }
            }
        });
    }//מוריד קודם את הUsersDetails קורא לעשות את השולחן

    public void setTableStartGame(){
        //usersArrListInGame מכיל את הUSERS אבל לא בסדר הנכון אז נסדר לפי המשחק עצמו
        //////// נשנה את הקלפים בFLOP ויזואלית
        cardFlopNumber1.setImageResource(0);
        cardFlopNumber2.setImageResource(0);
        cardFlopNumber3.setImageResource(0);
        cardFlopNumber4.setImageResource(0);
        cardFlopNumber5.setImageResource(0);
        myCardNumber1.setImageResource(0);
        myCardNumber2.setImageResource(0);
        player4constraintlayout.setBackgroundColor(Color.WHITE);
        player3constraintlayout.setBackgroundColor(Color.WHITE);
        player2constraintlayout.setBackgroundColor(Color.WHITE);
        player1constraintlayout.setBackgroundColor(Color.WHITE);


        //////////
        ArrayList<User> usersTemp=new ArrayList<>();
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                resultPokerTableGamesToFirstDeal=(HashMap<String, Object>)task.getResult().getValue();
                HashMap < String, String > usersDetails = (HashMap<String, String>) resultPokerTableGamesToFirstDeal.get("activeActions");
                for (Map.Entry<String,String> entry:usersDetails.entrySet()) {
                    for (int i = 0; i < usersArrListInGame.size(); i++) {
                        if (entry.getKey().replace("*",".").equals(usersArrListInGame.get(i).getEmail())){
                            usersTemp.add(usersArrListInGame.get(i));
                        }
                    }
                }
                usersArrListInGame=usersTemp;
                //אני תמיד מקדימה
                numOthersInTable=new int[usersArrListInGame.size()];//עשינו את המערך הסבר למעלה
                for (int i = 0; i < numOthersInTable.length; i++) {
                    if (myPlace==i){
                        numOthersInTable[i]=0;
                    }
                    else if(i<myPlace){
                        numOthersInTable[i]=i+1;
                    }
                    else {
                        numOthersInTable[i]=i;
                    }
                }
                int[] numOthers=new int[usersArrListInGame.size()-1];//מערך משמש לעיצוב בהתחלה
                for (int i = 0; i < numOthers.length; i++) {
                    if (myPlace==i){
                        numOthers[i]=i+1;
                        i++;
                        for (; i < numOthers.length; i++) {
                            numOthers[i]=i+1;
                        }
                    }
                    else{
                        numOthers[i]=i;
                    }
                }

                if (numOfPlayers==2){
                    //נתחיל עם שחקנים שמורידים
                    int numMoney1=0,numMoney2=0,i=0;
                    HashMap<String ,Integer> userDetails=(HashMap<String ,Integer>)(resultPokerTableGamesToFirstDeal.get("usersDetails"));
                    for(Map.Entry<String ,Integer> entry: userDetails.entrySet()){
                        if (usersArrListInGame.get(numOthers[0]).getEmail().equals(entry.getKey().replace("*","."))){
                            numMoney2=Integer.parseInt(""+entry.getValue());
                        }
                        else{
                            numMoney1=Integer.parseInt(""+entry.getValue());
                        }
                        //קשור למערך הכסף של כל אחד לשמירה רק
                        arrMoneyOfEveryPlayerInStartOfGame[i]=Integer.parseInt(""+entry.getValue());
                        i++;

                    }
                    player3constraintlayout.setVisibility(View.GONE);
                    player4constraintlayout.setVisibility(View.GONE);
                    player1constraintlayout.setVisibility(View.VISIBLE);
                    player2constraintlayout.setVisibility(View.VISIBLE);

                    player1TextViewMoney.setText(""+numMoney1);
                    player2TextViewMoney.setText(""+numMoney2);
                    player1TextViewName.setText(""+usersArrListInGame.get(myPlace).getUserName());
                    player2TextViewName.setText(""+usersArrListInGame.get(numOthers[0]).getUserName());
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(myPlace).getProfileImageUri()).into(player1ProfileImage);
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(numOthers[0]).getProfileImageUri()).into(player2ProfileImage);

                    tvRaisedplayer1.setText("");
                    tvRaisedplayer2.setText("");
                    btnWhatDidPlayer1Icon.setImageResource(0);
                    btnWhatDidPlayer2Icon.setImageResource(0);

                }
                else if (numOfPlayers==3){
                    int numMoney1=0,numMoney2=0,numMoney3=0,i=0;
                    HashMap<String ,Integer> userDetails=(HashMap<String ,Integer>)(resultPokerTableGamesToFirstDeal.get("usersDetails"));
                    for(Map.Entry<String ,Integer> entry: userDetails.entrySet()){
                        if (usersArrListInGame.get(numOthers[0]).getEmail().equals(entry.getKey().replace("*","."))){
                            numMoney2=Integer.parseInt(""+entry.getValue());
                        }
                        else if (usersArrListInGame.get(numOthers[1]).getEmail().equals(entry.getKey().replace("*","."))){
                            numMoney3=Integer.parseInt(""+entry.getValue());
                        }
                        else{
                            numMoney1=Integer.parseInt(""+entry.getValue());
                        }
                        //קשור למערך הכסף של כל אחד לשמירה רק
                        arrMoneyOfEveryPlayerInStartOfGame[i]=Integer.parseInt(""+entry.getValue());
                        i++;
                    }

                    player3constraintlayout.setVisibility(View.VISIBLE);
                    player4constraintlayout.setVisibility(View.GONE);
                    player1constraintlayout.setVisibility(View.VISIBLE);
                    player2constraintlayout.setVisibility(View.VISIBLE);

                    player1TextViewMoney.setText(""+numMoney1);
                    player2TextViewMoney.setText(""+numMoney2);
                    player1TextViewName.setText(""+usersArrListInGame.get(myPlace).getUserName());
                    player2TextViewName.setText(""+usersArrListInGame.get(numOthers[0]).getUserName());
                    player3TextViewMoney.setText(""+numMoney3);
                    player3TextViewName.setText(""+usersArrListInGame.get(numOthers[1]).getUserName());
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(myPlace).getProfileImageUri()).into(player1ProfileImage);
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(numOthers[0]).getProfileImageUri()).into(player2ProfileImage);
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(numOthers[1]).getProfileImageUri()).into(player3ProfileImage);

                    tvRaisedplayer1.setText("");
                    tvRaisedplayer2.setText("");
                    btnWhatDidPlayer1Icon.setImageResource(0);
                    btnWhatDidPlayer2Icon.setImageResource(0);
                    tvRaisedplayer3.setText("");
                    btnWhatDidPlayer3Icon.setImageResource(0);
                }
                else if (numOfPlayers==4){
                    int numMoney1=0,numMoney2=0,numMoney3=0,numMoney4=0,i=0;
                    HashMap<String ,Integer> userDetails=(HashMap<String ,Integer>)(resultPokerTableGamesToFirstDeal.get("usersDetails"));
                    for(Map.Entry<String ,Integer> entry: userDetails.entrySet()){
                        if (usersArrListInGame.get(numOthers[0]).getEmail().equals(entry.getKey().replace("*","."))){
                            numMoney2=Integer.parseInt(""+entry.getValue());
                        }
                        else if (usersArrListInGame.get(numOthers[1]).getEmail().equals(entry.getKey().replace("*","."))){
                            numMoney3=Integer.parseInt(""+entry.getValue());
                        }
                        else if (usersArrListInGame.get(numOthers[2]).getEmail().equals(entry.getKey().replace("*","."))){
                            numMoney4=Integer.parseInt(""+entry.getValue());
                        }
                        else{
                            numMoney1=Integer.parseInt(""+entry.getValue());
                        }
                        //קשור למערך הכסף של כל אחד לשמירה רק
                        arrMoneyOfEveryPlayerInStartOfGame[i]=Integer.parseInt(""+entry.getValue());
                        i++;
                    }

                    player3constraintlayout.setVisibility(View.VISIBLE);
                    player4constraintlayout.setVisibility(View.VISIBLE);
                    player1constraintlayout.setVisibility(View.VISIBLE);
                    player2constraintlayout.setVisibility(View.VISIBLE);

                    player1TextViewMoney.setText(""+numMoney1);
                    player2TextViewMoney.setText(""+numMoney2);
                    player1TextViewName.setText(""+usersArrListInGame.get(myPlace).getUserName());
                    player2TextViewName.setText(""+usersArrListInGame.get(numOthers[0]).getUserName());
                    player3TextViewMoney.setText(""+numMoney3);
                    player4TextViewMoney.setText(""+numMoney4);
                    player3TextViewName.setText(""+usersArrListInGame.get(numOthers[1]).getUserName());
                    player4TextViewName.setText(""+usersArrListInGame.get(numOthers[2]).getUserName());
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(myPlace).getProfileImageUri()).into(player1ProfileImage);
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(numOthers[0]).getProfileImageUri()).into(player2ProfileImage);
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(numOthers[1]).getProfileImageUri()).into(player3ProfileImage);
                    Glide.with(pokerGameActivity.this).load(usersArrListInGame.get(numOthers[2]).getProfileImageUri()).into(player4ProfileImage);

                    tvRaisedplayer1.setText("");
                    tvRaisedplayer2.setText("");
                    btnWhatDidPlayer1Icon.setImageResource(0);
                    btnWhatDidPlayer2Icon.setImageResource(0);
                    tvRaisedplayer3.setText("");
                    btnWhatDidPlayer3Icon.setImageResource(0);
                    tvRaisedplayer4.setText("");
                    btnWhatDidPlayer4Icon.setImageResource(0);
                }
            }
        });
        firstRoundDeal(myPlace);//מחלק לכולם קלפים
    } //חייב לעשות את הכל לפי הסדר אז: כאן שם שולחן לפי הDETAILS וקורא לסידור הקלפים
    public void firstRoundDeal(int yourTurn) {
            if (yourTurn == 0) {//אם תורי ואני ראשון מחלק לעצמי ישר וזהו
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {// לוקח את הCARDS בודק אם הזה שאחריי מוכן לקבל קלפים
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            Map<String, Object> cards = (Map) task.getResult().getValue();
                            ArrayList<HashMap> cardArrayList = (ArrayList<HashMap>) cards.get("cardsInDeck");
                            helpToFirstRoundDeal(cardArrayList);
                        }
                        catch (Exception e){
                            Toast.makeText(pokerGameActivity.this,"eror",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {//אם אנלא ראשון בודק אם הזה שלפני סיים אם לא ליסינר למתי שיסיים
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {// לוקח את הCARDS בודק אם הזה שאחריי מוכן לקבל קלפים
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            Map<String, Object> cards = (Map) task.getResult().getValue();
                            if (cards.containsKey("player"+(myPlace-1)+"done")){
                                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("player"+(myPlace-1)+"done").removeValue();
                                ArrayList<HashMap> cardArrayList = (ArrayList<HashMap>) cards.get("cardsInDeck");
                                helpToFirstRoundDeal(cardArrayList);
                            }
                            else{//אם הזה שלפניי לא קיבל אז מחכה
                                listener = dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("player" + (myPlace -1) + "done").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.getValue()!=null) {
                                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("player" + (myPlace - 1) + "done").removeEventListener(listener);
                                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {// לוקח את הCARDS
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    try {
                                                        Map<String, Object> cards = (Map) task.getResult().getValue();
                                                        ArrayList<HashMap> cardArrayList = (ArrayList<HashMap>) cards.get("cardsInDeck");
                                                        helpToFirstRoundDeal(cardArrayList);
                                                    } catch (Exception e) {
                                                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        } catch (Exception e) {
                            Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    //פעולה עוזרת לFIRSTROUND שמחלקת את הקלפים ומעדכנת DATABASE
    public void helpToFirstRoundDeal(ArrayList<HashMap> cardArrayList){
        ArrayList<Card> hand = new ArrayList<Card>();
        for (int i = 0; i < 2; i++) {
            int shape = (int) (Math.random() * 4);
            int num = (int) (Math.random() * 13);
            String shapeStr = "";
            if (shape == 1) {
                shapeStr = "p"; // עלה
            } else if (shape == 2) {
                shapeStr = "k"; // תלתן
            } else if (shape == 3) {
                shapeStr = "s"; // לב
            } else {
                shapeStr = "l";
            }
            hand.add(new Card(num+2, shapeStr));
        }
        int checkwoCards=0;
        HashMap<String, Object> mapTosave = new HashMap<String, Object>();
        for (int i = 0; i < 2; i++) {
            int num=hand.get(i).getNumOfValue();
            String shapeStr = hand.get(i).getSuit();
            for (int j = 0; j < cardArrayList.size(); j++) {
                HashMap<String, Object> map = cardArrayList.get(j);
                if (Integer.parseInt("" + map.get("numOfValue")) == num && ("" + map.get("suit")).equals(shapeStr)) {
                    cardArrayList.remove(map);
                    mapTosave=map;
                    checkwoCards++;
                    if(checkwoCards==2){
                        break;
                    }
                }
            }
        }
        if (checkwoCards==2) {
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child(mAuth.getCurrentUser().getEmail().replace(".", "*")).setValue(hand);//קודם משנה את שלי ואז מראה חפיסה לכולם
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("cardsInDeck").setValue(cardArrayList);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("player"+myPlace+"done").setValue("done");
            myHand = hand;
            displayHand();
            //לאחר החלוקה נעבור לבדיקה אם אחרון בתור וגם
            listenerToChangeTurn();
            if(myPlace==usersArrListInGame.size()-1){
                startRound(0);
            }
        }
        else {
            cardArrayList.add(mapTosave);
            helpToFirstRoundDeal(cardArrayList);
        }
    }

    public void startRound(int numRound){
        if (numRound==0){
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("roundsEnded").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //if (usersArrListInGame.get(0).getEmail().equals(mAuth.getCurrentUser().getEmail())){//אם אני מתחיל לא יכול להיות כי קוראת לרק לאחרון
                    //     yourTurnSetup();
                    //  }
                    // else{
                    callbtnicon.setAlpha(0.7f);
                    raisebtnIcon.setAlpha(0.7f);
                    foldbtnicon.setAlpha(0.7f);
                    callbtnicon.setClickable(false);
                    raisebtnIcon.setClickable(false);
                    foldbtnicon.setClickable(false);
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(0);
                }
            });
        }
    } //קוראת רק אם אתה האחרון בתור


    public void listenerToChangeTurn(){
        setListenerToChangeRound();
        listenerToEndGame();
        listenerToChangeTurn= dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    //מפעילLISTENER לבדיקות אם השתנה ROUND לא סוגר את זה כי לא צריך תמיד שישתנה הלוח ארצה להציגו
                    int numTurn = Integer.parseInt(""+dataSnapshot.getValue());
                    if (numTurn==0){
                        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("usersDetails").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                HashMap<String,Integer> usersDetails=(HashMap<String, Integer>) task.getResult().getValue();
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : usersDetails.entrySet()) {
                                    arrMoneyOfEveryPlayerInStartOfGame[i] = Integer.parseInt("" + entry.getValue());
                                    i++;
                                }
                            }
                        });
                    }
                    //כאילו כל פעם שיש תור 0 אז מוריד שוב מהפייר בייס שיעכל קודם את התנועות האחרות של השחקנים ואז יאתחל את הmoney
                    if (numTurn % usersArrListInGame.size() == myPlace) {
                        alreadyGetInChangedCards = false; //בסיבוב זה מראה שלא נכנסת לCARDS
                        numOfTurnsIHaveInSameRound++;
                        if (numTurn == 0 && numOfTurnsIHaveInSameRound==1) {
                            for (int i = 0; i < usersArrListInGame.size(); i++) {
                                //בתחילת כל סיבוב הראשון יאתחל את הכל
                                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(usersArrListInGame.get(i).getEmail().replace(".","*")).setValue("wait");
                            }
                            yourTurnSetup();
                        }
                        else if (numTurn != 0){
                            yourTurnSetup();
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Map<String,String> actionUsers = (Map<String,String>) task.getResult().getValue();
                                        int counter=0;
                                        String actionOneBefore = "";
                                        for (Map.Entry<String,String> entry:actionUsers.entrySet()){
                                            if (((numTurn-1)% usersArrListInGame.size())==counter){
                                                actionOneBefore=entry.getValue();
                                            }
                                            counter++;
                                        }
                                        if (actionOneBefore.contains("raise")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("raise:");
                                            int numRaise = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], numRaise, 1);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("fold")) {
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], 0, 4);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("check")) {
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], 0, 3);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        } //לבדוק כאן לגבי ה CHECK איך להציג
                                        else if (actionOneBefore.contains("call")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("call:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], numCall, 2);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        } else if (actionOneBefore.contains("allIn")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("allIn:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], numCall, 5);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }

                                    }
                                }
                            });
                            //צריך לפתוח את הACTIVE ולראות מה עשה מי שלפניי
                        }
                        else {
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Map<String,Object> actionUsers = (Map<String,Object>) task.getResult().getValue();
                                        String actionOneBefore = ""+actionUsers.get("lastToPlayAction");
                                        if (actionOneBefore.contains("raise")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("raise:");
                                            int numRaise = Integer.parseInt("" + actionOneBeforearr[1]);
                                            personBeforeActionVisual(numOthersInTable[(usersArrListInGame.size()-1)], numRaise, 1);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                        } else if (actionOneBefore.contains("fold")) {
                                                personBeforeActionVisual(numOthersInTable[(usersArrListInGame.size()-1)], 0, 4);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה

                                        } else if (actionOneBefore.contains("check")) {
                                                personBeforeActionVisual(numOthersInTable[(usersArrListInGame.size()-1)], 0, 3);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה

                                        } else if (actionOneBefore.contains("call")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("call:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                                personBeforeActionVisual(numOthersInTable[(usersArrListInGame.size()-1)], numCall, 2);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה

                                        } else if (actionOneBefore.contains("allIn")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("allIn:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                            personBeforeActionVisual(numOthersInTable[ usersArrListInGame.size()-1], numCall, 5);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                        }
                                        yourTurnSetup();
                                        doneRoundVisual();
                                    }
                                }
                            });

                        }

                    }
                    else {//אם לא תורי צריך עדיין להראות מה עשה הקודם
                        alreadyGetInChangedCards = false; //בסיבוב זה מראה שלא נכנסת לCARDS
                        ///כי תור מישהו אחר אז לא נכנסת
                        othersTurnSetup(numTurn % usersArrListInGame.size());
                        if (numTurn!=0) {
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Map actionUsers = (Map) task.getResult().getValue();
                                        String actionOneBefore = "" + actionUsers.get(usersArrListInGame.get((numTurn - 1) % usersArrListInGame.size()).getEmail().replace(".", "*"));
                                        if (actionOneBefore.contains("raise")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("raise:");
                                            int numRaise = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], numRaise, 1);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("fold")) {
                                                if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], 0, 4);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("check")) {
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], 0, 3);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("call")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("call:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], numCall, 2);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("allIn")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("allIn:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn - 1) % usersArrListInGame.size()], numCall, 5);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }

                                    }
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void othersTurnSetup(int numTurn){
        setCountDownTimerOthersTurn();//שם טיימר לתור של אחרים
        if (numOthersInTable[numTurn%4]==1){
            yourturniconplayer1.setVisibility(View.GONE);
            yourturniconplayer2.setVisibility(View.VISIBLE);
        }
        if (numOthersInTable[numTurn%4]==2){
            yourturniconplayer2.setVisibility(View.GONE);
            yourturniconplayer3.setVisibility(View.VISIBLE);

        }
        if (numOthersInTable[numTurn%4]==3){
            yourturniconplayer3.setVisibility(View.GONE);
            yourturniconplayer4.setVisibility(View.VISIBLE);

        }
        callbtnicon.setAlpha(0.7f);
        raisebtnIcon.setAlpha(0.7f);
        foldbtnicon.setAlpha(0.7f);
        callbtnicon.setClickable(false);
        raisebtnIcon.setClickable(false);
        foldbtnicon.setClickable(false);
    }

    public void personBeforeActionVisual(int numPersonThatDidActionInTable,int moneyTheAction,int numAction){
        //לא יכול להיות 0 כי במקום 0 אני נמצא
        //numAction 1=raise,2=call,3=check,4=fold;
        int numPersonThatDidActionInUsersArr=0;
        if (numPersonThatDidActionInTable==1){
            for (int i = 0; i < usersArrListInGame.size(); i++) {
                if (usersArrListInGame.get(i).getUserName().equals(""+player2TextViewName.getText().toString())){
                    numPersonThatDidActionInUsersArr=i;
                }
            }
        }
        else if (numPersonThatDidActionInTable==2){
            for (int i = 0; i < usersArrListInGame.size(); i++) {
                if (usersArrListInGame.get(i).getUserName().equals(""+player3TextViewName.getText().toString())){
                    numPersonThatDidActionInUsersArr=i;
                }
            }
        }
        else{
            for (int i = 0; i < usersArrListInGame.size(); i++) {
                if (usersArrListInGame.get(i).getUserName().equals(""+player4TextViewName.getText().toString())){
                    numPersonThatDidActionInUsersArr=i;
                }
            }
        }

        if (numAction==1) {
            int numPersonRaised = numPersonThatDidActionInTable;
            if (numPersonRaised == 1) {
                String moneyPlayer = ""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];//TODO שאני חוזר לשנות פה את כל הכסף ל כמו שכתוב פה
                //////////////////////////////////////////TODO
                int moneyBefore = Integer.parseInt("" + moneyPlayer);
                player2TextViewMoney.setText("" + (moneyBefore - moneyTheAction));
                btnWhatDidPlayer2Icon.setImageDrawable(getDrawable(R.drawable.raiseiconpoker));
                tvRaisedplayer2.setText("raise: " + moneyTheAction);
            }
            else if (numPersonRaised == 2) {
                String moneyPlayer = ""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                int moneyBefore = Integer.parseInt("" +moneyPlayer);
                player3TextViewMoney.setText("" + (moneyBefore - moneyTheAction));
                btnWhatDidPlayer3Icon.setImageDrawable(getDrawable(R.drawable.raiseiconpoker));
                tvRaisedplayer3.setText("raise: " + moneyTheAction);
            }
            else if (numPersonRaised == 3) {
                String moneyPlayer = ""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                int moneyBefore = Integer.parseInt("" + moneyPlayer);
                player4TextViewMoney.setText("" + (moneyBefore - moneyTheAction));
                btnWhatDidPlayer4Icon.setImageDrawable(getDrawable(R.drawable.raiseiconpoker));
                tvRaisedplayer4.setText("raise: " + moneyTheAction);
            }
        }
        else if (numAction==2){
            int numPersonCall= numPersonThatDidActionInTable;
            if (numPersonCall==1){
                    String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                    int moneyAction=moneyTheAction;
                    int moneyBefore=Integer.parseInt(""+moneyPlayer);
                    player2TextViewMoney.setText(""+(moneyBefore-moneyAction));
                    btnWhatDidPlayer2Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
                    tvRaisedplayer2.setText("call: "+moneyAction);
                }
            else if (numPersonCall==2){
                String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                int moneyAction=moneyTheAction;
                int moneyBefore=Integer.parseInt(""+moneyPlayer);
                player3TextViewMoney.setText(""+(moneyBefore-moneyAction));
                btnWhatDidPlayer3Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
                tvRaisedplayer3.setText("call: "+moneyAction);
                }
            else if (numPersonCall==3){
                String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                int moneyAction=moneyTheAction;
                int moneyBefore=Integer.parseInt(""+moneyPlayer);
                player4TextViewMoney.setText(""+(moneyBefore-moneyAction));
                btnWhatDidPlayer4Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
                tvRaisedplayer4.setText("call: "+moneyAction);
            }
            //מעצב מה קורה אם יש Call
        }
        else if (numAction==3){
            int numPersonCheck= numPersonThatDidActionInTable;
            if (numPersonCheck==1){
                player2TextViewMoney.setText("check");
                btnWhatDidPlayer2Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
            }
            else if (numPersonCheck==2){
                player3TextViewMoney.setText("check");
                btnWhatDidPlayer3Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
            }
            else if (numPersonCheck==3){
                player4TextViewMoney.setText("check");
                btnWhatDidPlayer4Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
            }
        }
        else if (numAction==4){
            int numPersonFold = numPersonThatDidActionInTable;
            if (numPersonFold==1){
                player2constraintlayout.setBackgroundColor(Color.RED);
                btnWhatDidPlayer2Icon.setImageDrawable(getDrawable(R.drawable.foldiconpoker));
            }
            else if (numPersonFold==2){
                player3constraintlayout.setBackgroundColor(Color.RED);
                btnWhatDidPlayer3Icon.setImageDrawable(getDrawable(R.drawable.foldiconpoker));
            }
            else if (numPersonFold==3){
                player4constraintlayout.setBackgroundColor(Color.RED);
                btnWhatDidPlayer4Icon.setImageDrawable(getDrawable(R.drawable.foldiconpoker));
            }
        }
        else if (numAction==5){
            int numPersonCall= numPersonThatDidActionInTable;
            if (numPersonCall==1){
                String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                int moneyAction=moneyTheAction;
                int moneyBefore=Integer.parseInt(""+moneyPlayer);
                player2TextViewMoney.setText(""+(moneyBefore-moneyAction));
                btnWhatDidPlayer2Icon.setImageDrawable(getDrawable(R.drawable.allinicon));
                tvRaisedplayer2.setText("allIn: "+moneyAction);
            }
            else if (numPersonCall==2){
                String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                int moneyAction=moneyTheAction;
                int moneyBefore=Integer.parseInt(""+moneyPlayer);
                player3TextViewMoney.setText(""+(moneyBefore-moneyAction));
                btnWhatDidPlayer3Icon.setImageDrawable(getDrawable(R.drawable.allinicon));
                tvRaisedplayer3.setText("allIn: "+moneyAction);
            }
            else if (numPersonCall==3){
                String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[numPersonThatDidActionInUsersArr];
                int moneyAction=moneyTheAction;
                int moneyBefore=Integer.parseInt(""+moneyPlayer);
                player4TextViewMoney.setText(""+(moneyBefore-moneyAction));
                btnWhatDidPlayer4Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
                tvRaisedplayer4.setText("allIn: "+moneyAction);
            }
            //מעצב מה קורה אם יש Call
        }


    }//פעולת עזר ויזואלית
    public void setCountDownTimerMyTurn(){
        countDownTimer=new CountDownTimer(25000, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(""+ (new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished))));
            }
            public void onFinish() {
                timerIsDoneMyTurn();
            }
        }.start();
    }//פעולה שמציגה את השעון ואם נגמר אז לא הספקתי אז קורא לעוד פעולה
    public void setCountDownTimerOthersTurn(){
        countDownTimer=new CountDownTimer(25000, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(""+ (new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished))));
            }
            public void onFinish() {
                textViewTimer.setText(""+ (new SimpleDateFormat("mm:ss").format(new Date(0))));
            }
        }.start();
    }//פעולה שמציגה את השעון אם נגמר לא קורה כלום
    public void yourTurnSetup(){
        //download game antil now then visible to start turn
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//לקחנו את כל המסמך
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(pokerGameActivity.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        HashMap<String, Object> resultPokerTableGames = (HashMap<String, Object>) task.getResult().getValue();
                        myTurnDownloadedAllDocument=resultPokerTableGames;
                        foldbtnicon.setAlpha((float) 1);
                        callbtnicon.setAlpha((float) 1);
                        raisebtnIcon.setAlpha((float) 1);
                        foldbtnicon.setClickable(true);
                        callbtnicon.setClickable(true);
                        raisebtnIcon.setClickable(true);
                        setCountDownTimerMyTurn();
                        yourturniconplayer1.setVisibility(View.VISIBLE);
                        yourturniconplayer2.setVisibility(View.GONE);
                        yourturniconplayer3.setVisibility(View.GONE);
                        yourturniconplayer4.setVisibility(View.GONE);

                    }
                    catch (Exception e){
                        Toast.makeText(pokerGameActivity.this,"eror",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }//מעצב את תורי ויזואלית



    public int checkMaxMinToRaiseAndCall(String action){//בודק אם יכול להעלות או צריך ALLIN בנוסף בודק אם צריך לעשות ALLIN או אםשר CALL
        HashMap<String,String>actionUsersBefore=(HashMap<String,String>)myTurnDownloadedAllDocument.get("activeActions");
        HashMap<String,Integer> usersDetails=(HashMap<String,Integer>)myTurnDownloadedAllDocument.get("usersDetails");
        ArrayList<Integer> actionUsersBeforeMoney=new ArrayList<Integer>();
        //////////CHECK FOR ALL INS ALL SO I CANT ALL IN
        //TODO לבדוק יום שני ה 20.12 מכאן
        int counter=0;
        for(Map.Entry<String,String> entry : actionUsersBefore.entrySet()) {
            if (entry.getValue().contains("allIn")){
                counter++;
            }
            else if (entry.getValue().contains("raise")){
                String[] arr=entry.getValue().split("raise:");
                if (Integer.parseInt(""+arr[1])>yourMoney){
                    counter++;
                }
            }
        }
        if (counter+1==usersArrListInGame.size()){
            if(action.equals("raise")) {
                player2constraintlayout.setVisibility(View.VISIBLE);
                constraintRaise.setVisibility(View.GONE);
                LinearLayoutToRaiseButton.setVisibility(View.GONE);
                cantRaiseOnlyAllIn("tryRaise");
                return -1;
            }
        }
        /////////////
        ArrayList<Integer> moneyUsers=new ArrayList<Integer>();

        for(Map.Entry<String,String> entry : actionUsersBefore.entrySet()) {
            if (!entry.getKey().replace("*",".").equals(mAuth.getCurrentUser().getEmail())) {//אפשר לא להחשיב אותי כי אם הגיעו אליי חזרה אז כנראה שהעלו יותר
                if (entry.getValue().equals("wait") || entry.getValue().contains("fold")) {
                    actionUsersBeforeMoney.add(0);
                    moneyUsers.add(Integer.parseInt(""+usersDetails.get(entry.getKey())));
                } else if (entry.getValue().contains("call")) {
                    String[] actionOneBeforearr = entry.getValue().split("call:");
                    int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                    actionUsersBeforeMoney.add(numCall);
                    moneyUsers.add(Integer.parseInt(""+usersDetails.get(entry.getKey()))+numCall);
                } else if (entry.getValue().contains("raise")) {
                    String[] actionOneBeforearr = entry.getValue().split("raise:");
                    int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                    actionUsersBeforeMoney.add(numCall);
                    moneyUsers.add(Integer.parseInt(""+usersDetails.get(entry.getKey()))+numCall);
                } else if (entry.getValue().contains("allIn")) {
                    String[] actionOneBeforearr = entry.getValue().split("allIn:");
                    int numAllIn = Integer.parseInt("" + actionOneBeforearr[1]);
                    actionUsersBeforeMoney.add(numAllIn);
                    moneyUsers.add(Integer.parseInt(""+usersDetails.get(entry.getKey()))+numAllIn);
                }
            }
        }
        Collections.sort(actionUsersBeforeMoney);//בודק מינימום
        //בודק מקסימום
        Collections.sort(moneyUsers);
        if (action.equals("raise")) {
            if (yourMoney < 10+actionUsersBeforeMoney.get(actionUsersBeforeMoney.size() - 1)) {//אם יש לי פחות מהמקסימום שהעלו
                cantRaiseOnlyAllIn("tryRaise");
                player2constraintlayout.setVisibility(View.VISIBLE);
                constraintRaise.setVisibility(View.GONE);
                LinearLayoutToRaiseButton.setVisibility(View.GONE);
            }
            else {
                if(moneyUsers.get(moneyUsers.size()-1)<Integer.parseInt(""+usersDetails.get(mAuth.getCurrentUser().getEmail().replace(".","*")))){ //אם הכי קטן של כולם קטן משל שלי אז
                    setSeekbarToRaise(actionUsersBeforeMoney.get(actionUsersBeforeMoney.size()-1) + 10, moneyUsers.get(moneyUsers.size()-1) );//צריך שהמינימום לRAISE יהיה המינימום +10

                }
                else{
                    setSeekbarToRaise(actionUsersBeforeMoney.get(actionUsersBeforeMoney.size()-1) + 10, Integer.parseInt(""+usersDetails.get(mAuth.getCurrentUser().getEmail().replace(".","*"))) );//צריך שהמינימום לRAISE יהיה המינימום +10

                }
            }
        }
        else{
            if (yourMoney <= actionUsersBeforeMoney.get(0)) {//אם יש לי פחות מהמקסימום שהעלו
                cantRaiseOnlyAllIn("call");
            }
            else {
                return actionUsersBeforeMoney.get(actionUsersBeforeMoney.size() - 1);
            }
        }
        return -1;//אם הRAISE קורא לו אז לא משנה הBOOLEAN
    }//הBOOLEAN הוא משמש לCALL ONCLICK
    public void cantRaiseOnlyAllIn(String action){
        if (action.equals("tryRaise")){
            Toast.makeText(this,"You can only call to all in",Toast.LENGTH_SHORT);
        }
        else{
            if (action.equals("call")){
                HashMap<String,Integer> usersDetails=(HashMap<String,Integer>)myTurnDownloadedAllDocument.get("usersDetails");
                HashMap<String,String> activeActions=(HashMap<String,String>)myTurnDownloadedAllDocument.get("activeActions");
                String str=(activeActions.get(mAuth.getCurrentUser().getEmail().replace(".","*")));
                int moneyInTable;
                if (str.contains("wait")) {
                    moneyInTable = 0;
                }
                else{
                    String[] strings=str.split(":");
                    moneyInTable=Integer.parseInt(strings[1]);
                }

                int numAfter=Integer.parseInt(""+usersDetails.get(mAuth.getCurrentUser().getEmail().replace(".","*")));
                int num=numAfter+moneyInTable;
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").child(mAuth.getCurrentUser().getEmail().replace(".","*")).setValue("allIn:"+num);
                yourMoney=0;
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(mAuth.getCurrentUser().getEmail().replace(".","*")).setValue(0);
                checkIfDoneRoundAndGame();//שולח לבדוקה ומשם לסיום התור
            }
        }
        canDoOnlyAllIn=true;
    }
    public void setSeekbarToRaise(int min,int max){
        SeekbarRaise.setMax(max/10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SeekbarRaise.setMin(min/10);
        }
        SeekbarRaise.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvRaise.setText(progress*10+"$");
                //  Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //if (tvRaise.getText().toString().split("$")[0].equals(""+min)){//אם המחיר הכי נמוך אז להוריד את המחיר
                //    tvRaise.setText("");
               // }
            }
        });
    }
    public void displayHand(){//לבדוק אם להוריד תוך כדי קלפים לפרוייקט ולשמור ב SHERED
        int num = myHand.get(0).getNumOfValue();
        String numString1="",numString2="";
        if(num>10){
            if (num==11){
                numString1="j";
            }
            else if (num==12){
                numString1="q";
            }
            else if (num==13){
                numString1="k";
            }
            else if (num==14){
                numString1="a";
            }
        }
        else{
            numString1=""+num;
        }
        if (myHand.get(1).getNumOfValue()>10){
            if (myHand.get(1).getNumOfValue()==11){
                numString2="j";
            }
            else if (myHand.get(1).getNumOfValue()==12){
                numString2="q";
            }
            else if (myHand.get(1).getNumOfValue()==13){
                numString2="k";
            }
            else if (myHand.get(1).getNumOfValue()==14){
                numString2="a";
            }
        }
        else{
            numString2=""+myHand.get(1).getNumOfValue();
        }
        String shape = myHand.get(0).getSuit();
        storageRef.child("profilePicturs/pokerCards/" + shape + numString1 + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(pokerGameActivity.this).load(uri).into(myCardNumber1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
            }
        });
        storageRef.child("profilePicturs/pokerCards/" + (myHand.get(1).getSuit()) + numString2 + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(pokerGameActivity.this).load(uri).into(myCardNumber2);
            }
        });
    }//לבדוק אם להוריד תוך כדי קלפים לפרוייקט ולשמור ב SHERED
    public void MyActionVisual(int numAction,int moneyTheAction){
        if (numAction==1) {

            String moneyPlayer = ""+arrMoneyOfEveryPlayerInStartOfGame[myPlace];
            int moneyBefore = Integer.parseInt("" + moneyPlayer);
            player1TextViewMoney.setText("" + (moneyBefore - moneyTheAction));
            btnWhatDidPlayer1Icon.setImageDrawable(getDrawable(R.drawable.raiseiconpoker));
            tvRaisedplayer1.setText("raise: " + moneyTheAction);
        }
        else if (numAction==2){
            String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[myPlace];
            int moneyAction=moneyTheAction;
            int moneyBefore=Integer.parseInt(""+moneyPlayer);
            player1TextViewMoney.setText(""+(moneyBefore-moneyAction));
            btnWhatDidPlayer1Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
            tvRaisedplayer1.setText("call: "+moneyAction);
            //מעצב מה קורה אם יש Call
        }
        else if (numAction==3){
            player1TextViewMoney.setText("check");
            btnWhatDidPlayer1Icon.setImageDrawable(getDrawable(R.drawable.calliconpoker));
        }
        else if (numAction==4){
            player1constraintlayout.setBackgroundColor(Color.RED);
            btnWhatDidPlayer1Icon.setImageDrawable(getDrawable(R.drawable.foldiconpoker));
        }
        else if (numAction==5){
            String moneyPlayer=""+arrMoneyOfEveryPlayerInStartOfGame[myPlace];
            int moneyAction=moneyTheAction;
            int moneyBefore=Integer.parseInt(""+moneyPlayer);
            player1TextViewMoney.setText(""+(moneyBefore-moneyAction));
            btnWhatDidPlayer1Icon.setImageDrawable(getDrawable(R.drawable.allinicon));
            tvRaisedplayer1.setText("allIn: "+moneyAction);
            //ALLIN
        }
    }

        //TODO: להוסיף בדיקה של הוספת עוד קופה
    public void doneTurn(boolean doneRound,boolean doneGame,HashMap<String,Object> PokerTableGame){
        countDownTimer.cancel();
        if(doneRound){//להביא הכל לקופה ולבדוק אם צריך עוד אחת
            HashMap<String, String> resultPokerTableGames = (HashMap<String, String>)PokerTableGame.get("activeActions");
            int sumToAddKupa=0;
            String myAction="";
            for(Map.Entry<String,String> entry : resultPokerTableGames.entrySet()) {
                if(entry.getValue().contains("raise")){
                    String[] arr = entry.getValue().split("raise:");
                    int money = Integer.parseInt("" + arr[1]);
                    sumToAddKupa=sumToAddKupa+money;
                }
                else if (entry.getValue().contains("call")){
                    String[] arr = entry.getValue().split("call:");
                    int money = Integer.parseInt("" + arr[1]);
                    sumToAddKupa=sumToAddKupa+money;
                }
                if (entry.getKey().equals(mAuth.getCurrentUser().getEmail().replace(".","*"))){
                    myAction= entry.getValue();
                }
                //TODO להמשיך כאן לבדוק למה lastToPlayAction לא עובד
            }
            HashMap<String, Integer> tableDetails=(HashMap<String, Integer>)PokerTableGame.get("tableDetails");
            int numInKupa=Integer.parseInt(""+tableDetails.get("Kupa"));
            int numSum=numInKupa+sumToAddKupa;
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("Kupa").setValue(numSum);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("lastToPlayAction").setValue(myAction);
            dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("lastToPlayBeforeChangeRound").setValue(""+myPlace);
            changeRoundCardsWhenLast(PokerTableGame);//משנה גם את הROUND וTURN
            //משנה את הACTIVE של כל אחד

        }
        else if (!doneGame&& !doneRound){
         //   HashMap<String, Integer> tableDetails=(HashMap<String, Integer>)PokerTableGame.get("tableDetails");
            HashMap<String, String> resultPokerTableGames = (HashMap<String, String>)PokerTableGame.get("activeActions");
            int numPasses=0,numPlayerNext=0;
            boolean found=false;
            for (int i = 0; i < 2; i++) {
                for (Map.Entry<String, String> entry : resultPokerTableGames.entrySet()) {
                    if (numPasses > myPlace && i==0) {//סיבוב ראשון
                        if (entry.getValue().contains("fold") || entry.getValue().contains("allIn")) {}
                        else {
                            numPlayerNext=numPasses%usersArrListInGame.size();
                            found=true;
                            break;
                        }
                    }
                    else if (i==1){
                        if (entry.getValue().contains("fold") || entry.getValue().contains("allIn")) {}
                        else {
                            numPlayerNext=numPasses%usersArrListInGame.size();
                            found=true;
                            break;
                        }
                    }
                    numPasses++;
                }
                numPasses=0;
                if (found){
                    break;
                }
            }
            //אם הזה שאחרי יכול לשחק
            if (myPlace==usersArrListInGame.size()-1) {
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(numPlayerNext + ((numOfTurnsIHaveInSameRound) * usersArrListInGame.size()));
            }
            else{
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(numPlayerNext + ((numOfTurnsIHaveInSameRound-1) * usersArrListInGame.size()));
            }
        }
        if (doneGame){
            HashMap<String, String> resultPokerTableGames = (HashMap<String, String>)PokerTableGame.get("activeActions");
            HashMap<String, Object> tableDetails = (HashMap<String, Object>)PokerTableGame.get("tableDetails");
            int numRound=Integer.parseInt(""+tableDetails.get("roundsEnded"));
            int numCheck=0, numAllIn=0;
            for (Map.Entry<String,String> entry:resultPokerTableGames.entrySet()) {
                if (entry.getValue().contains("fold")){
                    numCheck++;
                }
                else if(entry.getValue().contains("allIn")) {
                    numAllIn++;
                }
            }
            if (usersArrListInGame.size()-numCheck>1){
                //כשלא נגמר בגלל FOLD
                if (numAllIn>0) {
                    //כשמגמר בALLIN
                    if (numRound < 3) {//כלומר יש USERS וגם נגמר המשחק לפני 3 סיבובים לכן זה נגמר ב ALLIN
                        String myAction = "";
                        int sumToAddKupa = 0;
                        for (Map.Entry<String, String> entry : resultPokerTableGames.entrySet()) {
                            if (entry.getValue().contains("raise")) {
                                String[] arr = entry.getValue().split("raise:");
                                int money = Integer.parseInt("" + arr[1]);
                                sumToAddKupa = sumToAddKupa + money;
                            }
                            if (entry.getValue().contains("allIn")) {
                                String[] arr = entry.getValue().split("allIn:");
                                int money = Integer.parseInt("" + arr[1]);
                                sumToAddKupa = sumToAddKupa + money;
                            } else if (entry.getValue().contains("call")) {
                                String[] arr = entry.getValue().split("call:");
                                int money = Integer.parseInt("" + arr[1]);
                                sumToAddKupa = sumToAddKupa + money;
                            }
                            if (entry.getKey().equals(mAuth.getCurrentUser().getEmail().replace(".", "*"))) {
                                myAction = entry.getValue();
                            }
                            //TODO להמשיך כאן לבדוק למה lastToPlayAction לא עובד
                        }
                        HashMap<String, Integer> tableDetails2 = (HashMap<String, Integer>) PokerTableGame.get("tableDetails");
                        int numInKupa = Integer.parseInt("" + tableDetails2.get("Kupa"));
                        int numSum = numInKupa + sumToAddKupa;
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("Kupa").setValue(numSum);
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(myPlace + 1);
                        checkIfNeedToOpenAllFlopWhenAllIn(numRound, PokerTableGame, myAction);
                        //שולח להשוואה מי מנצח מתוך הפעולה הזאת
                    }
                    else {
                        int sumToAddKupa = 0;
                        String myAction = "";
                        for (Map.Entry<String, String> entry : resultPokerTableGames.entrySet()) {
                            if (entry.getValue().contains("raise")) {
                                String[] arr = entry.getValue().split("raise:");
                                int money = Integer.parseInt("" + arr[1]);
                                sumToAddKupa = sumToAddKupa + money;
                            }
                            if (entry.getValue().contains("allIn")) {
                                String[] arr = entry.getValue().split("allIn:");
                                int money = Integer.parseInt("" + arr[1]);
                                sumToAddKupa = sumToAddKupa + money;
                            } else if (entry.getValue().contains("call")) {
                                String[] arr = entry.getValue().split("call:");
                                int money = Integer.parseInt("" + arr[1]);
                                sumToAddKupa = sumToAddKupa + money;
                            }
                            if (entry.getKey().equals(mAuth.getCurrentUser().getEmail().replace(".", "*"))) {
                                myAction = entry.getValue();
                            }
                            //TODO להמשיך כאן לבדוק למה lastToPlayAction לא עובד
                        }
                        HashMap<String, Integer> tableDetails2 = (HashMap<String, Integer>) PokerTableGame.get("tableDetails");
                        int numInKupa = Integer.parseInt("" + tableDetails2.get("Kupa"));
                        int numSum = numInKupa + sumToAddKupa;

                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("Kupa").setValue(numSum);
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(myPlace + 1);
                        //endGameWithUsers2Players(PokerTableGame);
                        checkIfNeedToOpenAllFlopWhenAllIn(numRound, PokerTableGame, myAction);
                        //שולח להשוואה מי מנצח
                    }
                }
                else {
                    //שנגמר רגיל
                    int sumToAddKupa = 0;
                    String myAction = "";
                    for (Map.Entry<String, String> entry : resultPokerTableGames.entrySet()) {
                        if (entry.getValue().contains("raise")) {
                            String[] arr = entry.getValue().split("raise:");
                            int money = Integer.parseInt("" + arr[1]);
                            sumToAddKupa = sumToAddKupa + money;
                        }
                        if (entry.getValue().contains("allIn")) {
                            String[] arr = entry.getValue().split("allIn:");
                            int money = Integer.parseInt("" + arr[1]);
                            sumToAddKupa = sumToAddKupa + money;
                        } else if (entry.getValue().contains("call")) {
                            String[] arr = entry.getValue().split("call:");
                            int money = Integer.parseInt("" + arr[1]);
                            sumToAddKupa = sumToAddKupa + money;
                        }
                        if (entry.getKey().equals(mAuth.getCurrentUser().getEmail().replace(".", "*"))) {
                            myAction = entry.getValue();
                        }
                    }
                    HashMap<String, Integer> tableDetails2 = (HashMap<String, Integer>) PokerTableGame.get("tableDetails");
                    int numInKupa = Integer.parseInt("" + tableDetails2.get("Kupa"));
                    int numSum = numInKupa + sumToAddKupa;
                    //אוסף את הכסף של התור האחרון

                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("Kupa").setValue(numSum);
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(myPlace + 1);
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("lastToPlayAction").setValue(myAction);
                    dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("lastToPlayBeforeChangeRound").setValue(""+myPlace);
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("roundsEnded").setValue(3);
                    endGameWithUsers2Players(PokerTableGame);
                }
            }
            else{
                int sumToAddKupa=0;
                String myAction="";
                for(Map.Entry<String,String> entry : resultPokerTableGames.entrySet()) {
                    if(entry.getValue().contains("raise")){
                        String[] arr = entry.getValue().split("raise:");
                        int money = Integer.parseInt("" + arr[1]);
                        sumToAddKupa=sumToAddKupa+money;
                    }
                    else if (entry.getValue().contains("call")){
                        String[] arr = entry.getValue().split("call:");
                        int money = Integer.parseInt("" + arr[1]);
                        sumToAddKupa=sumToAddKupa+money;
                    }
                    if (entry.getKey().equals(mAuth.getCurrentUser().getEmail().replace(".","*"))){
                        myAction= entry.getValue();
                    }
                    //TODO להמשיך כאן לבדוק למה lastToPlayAction לא עובד
                }
                HashMap<String, Integer> tableDetails2=(HashMap<String, Integer>)PokerTableGame.get("tableDetails");
                int numInKupa=Integer.parseInt(""+tableDetails2.get("Kupa"));
                int numSum=numInKupa+sumToAddKupa;
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(myPlace+1);
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("lastToPlayAction").setValue(myAction);
                dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("lastToPlayBeforeChangeRound").setValue(""+myPlace);
                dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("Kupa").setValue(numSum).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    HashMap<String,Object> PokerTableGameUpdated= (HashMap<String,Object>)task.getResult().getValue();
                                    endGameWhenFolds(PokerTableGameUpdated);
                                }
                            });
                        }
                    }
                });
            }
        }

        {
            HashMap<String, String> resultPokerTableGames = (HashMap<String, String>)PokerTableGame.get("activeActions");
            String action=resultPokerTableGames.get(mAuth.getCurrentUser().getEmail().replace(".","*"));
            if (action.contains("raise")){
                String[] actionOneBeforearr = action.split("raise:");
                int numRaise = Integer.parseInt("" + actionOneBeforearr[1]);
                MyActionVisual(1,numRaise);
            }
            else if (action.contains("fold")) {
                MyActionVisual(4,0);
            }
            else if (action.contains("check")) {
                MyActionVisual(3,0);
            }
            else if (action.contains("call")) {
                String[] actionOneBeforearr = action.split("call:");
                int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                MyActionVisual(2,numCall);
            }
            else if (action.contains("allIn")) {
                String[] actionOneBeforearr = action.split("allIn:");
                int numALLIN = Integer.parseInt("" + actionOneBeforearr[1]);
                MyActionVisual(5,numALLIN);

            }

        }//עושה את מה שעשיתי ויזואלית

    }//לדאוג גם לבדוק אם הבא יכול לשחק

    public void changeRoundCardsWhenLast(HashMap<String,Object> PokerTableGame){
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue()!=null) {
                    try {
                        checkIfIEndRound=true;//משתנה כדי לדעת אם סיימתי סיבוב וככה לא צריך להציג מה עשה מי שסיים סיבוב
                        Map<String, Object> table = (Map) task.getResult().getValue();
                        Map<String, Object> cards = (Map<String, Object>) table.get("Cards");
                        ArrayList<HashMap> cardsBoard = new ArrayList<HashMap>();
                        if(cards.get("CardsBoard")!= null) {
                            cardsBoard = (ArrayList<HashMap>) cards.get("CardsBoard");
                        }
                        ArrayList<Card> board = new ArrayList<Card>();
                        for (int i = 0; i < cardsBoard.size(); i++) {
                            int numCard=Integer.parseInt(""+ cardsBoard.get(i).get("numOfValue"));
                            String suit=""+ cardsBoard.get(i).get("suit");
                            Card card=new Card(numCard,suit);
                            board.add(card);
                        }
                        ArrayList<HashMap> cardsInDeck = (ArrayList<HashMap>) cards.get("cardsInDeck");

                        int index=3;//בודק כמה קלפים צריך להוסיף
                        if (board.size()>2){
                            index=1;
                        }
                        for (int i = 0; i < index; i++) {
                            int numOfCard = (int) (Math.random() * cardsInDeck.size());
                            HashMap<String, Object> cardHash = cardsInDeck.get(numOfCard);
                            int num = Integer.parseInt(""+cardHash.get("numOfValue"));
                            String shape = ""+cardHash.get("suit");
                            board.add(new Card(num, shape));
                            cardsInDeck.remove(numOfCard);
                        }
                        //משהו לעזר
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("cardsInDeck").setValue(cardsInDeck);
                        HashMap<String,Long> mapTableDetails=(HashMap<String,Long>)PokerTableGame.get("tableDetails");
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("roundsEnded").setValue((Integer.parseInt(""+mapTableDetails.get("roundsEnded"))+1));
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("CardsBoard").setValue(board);
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("turn").setValue(0);

                    } catch (Exception e) {
                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                    }
                }//TODO להריץ מפה מחר לבדוק שהחלפת הסיבוב עובדת
            }
        });
    }

    public void setListenerToChangeRound(){
        listenerToChangeRound=dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("Cards").child("CardsBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        numOfTurnsIHaveInSameRound=0;
                        numRound++;
                        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.getResult().getValue()!=null) {
                                    HashMap<String, Object> allDetails = (HashMap<String, Object>) task.getResult().getValue();
                                    HashMap<String, Object> tableDetails = (HashMap<String, Object>) allDetails.get("tableDetails");
                                    //////////////////// כאן משנה את ה ARRMONEY בכל תחילת ROUND

                                    ///////////////////////
                                    if (checkIfIEndRound) {//נמצא במקום של בניית הCARDBOARD ששם מאותחל לTRUE אם הוא TRUE סימן שהחלפתי קלפים ואני החלפתי סיבוב ולא צריך ELSE אחרת ישאר FALSE
                                    } //אם אני אחרון
                                    //מציג מה קרה לקודם שהיה לפני הזה שסיים תור
                                    else {//נכנס לכאן רק כאשר לא אני שיניתי את הקלפים ואני צריך לשנות ויזואלית מה הזה ששינה קלפים עזה
                                        HashMap<String, String> map = (HashMap<String, String>) allDetails.get("tableDetails");
                                        int numTurn = Integer.parseInt("" + map.get("lastToPlayBeforeChangeRound"));
                                        String actionOneBefore = "" + map.get("lastToPlayAction");
                                        if (actionOneBefore.contains("raise")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("raise:");
                                            int numRaise = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn - 1) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn) % usersArrListInGame.size()], numRaise, 1);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("fold")) {
                                            if ((numTurn) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn) % usersArrListInGame.size()], 0, 4);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("check")) {
                                            if ((numTurn) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn) % usersArrListInGame.size()], 0, 3);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("call")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("call:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if (numTurn % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn) % usersArrListInGame.size()], numCall, 2);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        else if (actionOneBefore.contains("allIn")) {
                                            String[] actionOneBeforearr = actionOneBefore.split("allIn:");
                                            int numCall = Integer.parseInt("" + actionOneBeforearr[1]);
                                            if ((numTurn) % usersArrListInGame.size() != myPlace) {//אם תור מי שאחרי אין לי מה לשנות כי אני שיניתי כבר אצלי
                                                personBeforeActionVisual(numOthersInTable[(numTurn) % usersArrListInGame.size()], numCall, 5);//נותן את השחקן ויזואלית כלומרר איפה הוא ממוקם על הלוח 1 2 3
                                                //עובד בדקתי נותן את המיקום ואת המספר העלאה
                                            }
                                        }
                                        //לאחר שהשתמשתי בפעולות של האחרון אעשה REMOVE
                                        //תיקון לא לעשות מחיקהה כי אם עוד שחקן אז זה ימחק לו
                                        /*
                                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("lastToPlayBeforeChangeRound").removeValue();
                                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("lastToPlayAction").removeValue();
                                         */
                                    }
                                    ArrayList<HashMap> cardsBoard = (ArrayList<HashMap>) dataSnapshot.getValue();
                                    if (!alreadyGetInChangedCards) {
                                        alreadyGetInChangedCards=true;
                                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(pokerGameActivity.this, "conect to wifi first", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    try {
                                                        HashMap<String, Object> PokerTableGame = (HashMap<String, Object>) task.getResult().getValue();
                                                        HashMap<String, Integer> tableDetails = (HashMap<String, Integer>) PokerTableGame.get("tableDetails");
                                                        int numRound = Integer.parseInt("" + tableDetails.get("roundsEnded"));
                                                        HashMap<String, Boolean> tableDetails2 = (HashMap<String, Boolean>) PokerTableGame.get("tableDetails");
                                                        boolean endedWithAllIn=false;
                                                        try {
                                                            endedWithAllIn = tableDetails2.get("endedWithAllIn");
                                                        }
                                                        catch (Exception e){}
                                                        displayCardBoard(cardsBoard, numRound, 0,endedWithAllIn);
                                                        doneRoundVisual();
                                                        checkIfIEndRound = false;//אני לאחר ששיניתי תסיבוב
                                                        //משנה חזרה את ה arrMoneyOfEveryPlayerInStartOfGame
                                                        /*
                                                        HashMap<String, Integer> usersDetails = (HashMap<String, Integer>) PokerTableGame.get("usersDetails");
                                                        int i = 0;
                                                        for (Map.Entry<String, Integer> entry : usersDetails.entrySet()) {
                                                            arrMoneyOfEveryPlayerInStartOfGame[i] = Integer.parseInt("" + entry.getValue());
                                                            i++;
                                                        }
                                                         */
                                                    } catch (Exception e) {
                                                        Toast.makeText(pokerGameActivity.this, "eror with deal board", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                    catch (Exception e){
                        Toast.makeText(pokerGameActivity.this, "eror with deal board", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
            }

        });
    }

    //עובד לCALL
    public void checkIfDoneRoundAndGame(){//לממש כאן להמשיך בדיקות
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(pokerGameActivity.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        HashMap<String,Object> map=(HashMap<String, Object>) task.getResult().getValue();
                        HashMap<String, String> resultPokerTableGames = (HashMap<String, String>)map.get("activeActions");
                        //////////// בדיקה לגבי אם אני FOLD ואני אחד לפני אחרון במשחק
                        int checkFolds=0,checkAllIns=0;
                        for(Map.Entry<String,String> Entry : resultPokerTableGames.entrySet()) {
                            if (Entry.getValue().contains("fold")){
                                checkFolds++;
                            }
                            if(Entry.getValue().contains("allIn")){
                                checkAllIns++;
                            }
                        }
                        if (checkFolds==usersArrListInGame.size()-1){
                            doneTurn(false,true,map);
                            return;
                        }
                        else if (checkAllIns==usersArrListInGame.size()){
                            doneTurn(false,true,map);
                            return;
                        }
                        //////////////
                        int maxRaised=0;
                        boolean everyOnePlayed=true;
                        for(Map.Entry<String,String> entry : resultPokerTableGames.entrySet()) {
                            if (entry.getValue().equals("wait")){
                                everyOnePlayed=false;
                                break;
                            }
                            else if (entry.getValue().contains("raise")){//אם שחקן מסויים העלה אז לקחת את ההעלעה הגבוהה ביותר
                                String[] raise=entry.getValue().split("raise:");
                                int numRaise=Integer.parseInt(""+raise[1]);
                                if (numRaise>maxRaised){
                                    maxRaised=numRaise;
                                }
                            }
                        }
                        if (everyOnePlayed){
                            for(Map.Entry<String,String> entry : resultPokerTableGames.entrySet()) {
                                if (entry.getValue().contains("raise")){
                                    String[] raise=entry.getValue().split("raise:");
                                    int numRaise=Integer.parseInt(""+raise[1]);
                                    if (maxRaised!=numRaise) {
                                        doneTurn(false,false,map);//כלומר יש מישהו שהעלה ואחד שהעלה אחריו
                                        return;
                                    }//לבדוק אם מישהו עשה RERAISED
                                    //לבדוק אם מישהו שעשה קול נמוך מהRAise
                                }
                                else if (entry.getValue().contains("call:")){
                                    String[] call=entry.getValue().split("call:");
                                    int numCall=Integer.parseInt(""+call[1]);
                                    if (numCall<maxRaised){
                                        doneTurn(false,false,map);//כלומר יש מישהו שעשה קול אבל לא לגבוה ביותר
                                        return;
                                    }
                                }
                            }
                            //אם הגענו לכאן סימן שאו עשו הכל טוב כמו RAISEMAX וCALLMAX או שעשו ALLIN או FOLD
                            //כלומר נגמר סיבוב
                            HashMap<String, Integer> tableDetails=(HashMap<String, Integer>)map.get("tableDetails");
                            int numRound=Integer.parseInt(""+tableDetails.get("roundsEnded"));
                            if (numRound==3){
                                doneTurn(false,true,map);//כלומר גם נגמר סיבוב וגם נגמר משחק
                            }
                            else{
                                doneTurn(true,false,map);//כלומר רק נגמר משחק
                            }
                        }
                        else{
                            doneTurn(false,false,map);
                        }

                    }
                    catch (Exception e){
                        Toast.makeText(pokerGameActivity.this,"eror",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
        );
    }

    //להמשיך מחר יום רביעי לסיים
    //עובד
    public void checkIfNeedToOpenAllFlopWhenAllIn(int numRoundThatTheGameEnded,HashMap<String,Object> PokerTableGame,String myAction){
        HashMap<String,Object> cardsDetails= (HashMap<String,Object>)PokerTableGame.get("Cards");
        ArrayList<HashMap> cardsBoard = new ArrayList<HashMap>();
        if(cardsDetails.get("CardsBoard")!= null) {
            cardsBoard = (ArrayList<HashMap>) cardsDetails.get("CardsBoard");
        }
        ArrayList<Card> board = new ArrayList<Card>();
        for (int i = 0; i < cardsBoard.size(); i++) {
            int numCard=Integer.parseInt(""+ cardsBoard.get(i).get("numOfValue"));
            String suit=""+ cardsBoard.get(i).get("suit");
            Card card=new Card(numCard,suit);
            board.add(card);
        }
        checkIfIEndRound=true;
        ArrayList<HashMap> cardsInDeck = (ArrayList<HashMap>) cardsDetails.get("cardsInDeck");

        if (numRoundThatTheGameEnded==0) {
            for (int i = 0; i < 5; i++) {
                int numOfCard = (int) (Math.random() * cardsInDeck.size());
                HashMap<String, Object> cardHash = cardsInDeck.get(numOfCard);
                int num = Integer.parseInt(""+cardHash.get("numOfValue"));
                String shape = ""+cardHash.get("suit");
                board.add(new Card(num, shape));
                cardsInDeck.remove(numOfCard);
            }
        }
        else if (numRoundThatTheGameEnded==1) {
            for (int i = 0; i < 2; i++) {
                int numOfCard = (int) (Math.random() * cardsInDeck.size());
                HashMap<String, Object> cardHash = cardsInDeck.get(numOfCard);
                int num = Integer.parseInt(""+cardHash.get("numOfValue"));
                String shape = ""+cardHash.get("suit");
                board.add(new Card(num, shape));
                cardsInDeck.remove(numOfCard);
            }
        }
        else if (numRoundThatTheGameEnded==2) {
            for (int i = 0; i < 1; i++) {
                int numOfCard = (int) (Math.random() * cardsInDeck.size());
                HashMap<String, Object> cardHash = cardsInDeck.get(numOfCard);
                int num = Integer.parseInt(""+cardHash.get("numOfValue"));
                String shape = ""+cardHash.get("suit");
                board.add(new Card(num, shape));
                cardsInDeck.remove(numOfCard);
            }
        }
        else if (numRoundThatTheGameEnded==3) {
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("lastToPlayAction").setValue(myAction);
            dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("lastToPlayBeforeChangeRound").setValue(""+myPlace);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("roundsEnded").setValue(3);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endedWithAllIn").setValue(true);
            endGameWithUsers2Players(PokerTableGame);
        }
        //משהו לעזר
        if(numRoundThatTheGameEnded!=3) {
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("lastToPlayAction").setValue(myAction);
            dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("lastToPlayBeforeChangeRound").setValue(""+myPlace);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("cardsInDeck").setValue(cardsInDeck);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("roundsEnded").setValue(3);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endedWithAllIn").setValue(true);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("CardsBoard").setValue(board).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("CardsBoard").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            ArrayList<HashMap> board=(ArrayList<HashMap> )task.getResult().getValue();
                                            displayCardBoard(board,3,0,true);
                                        }
                                    });

                                }
                            }).start();

                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            HashMap<String,Object> map=(HashMap<String,Object>)task.getResult().getValue();
                            endGameWithUsers2Players(map);
                        }
                    });
                }
            });
        }



    }


    public void displayCardBoard(ArrayList<HashMap> board,int numOfRound,int indexToRecurtion,boolean ifNeenAllCards ){
        if (ifNeenAllCards){
            String suit = "" + board.get(indexToRecurtion).get("suit");
            storageRef.child("profilePicturs/pokerCards/" + suit + helpToDisplayCardBoard(Integer.parseInt("" + board.get(indexToRecurtion).get("numOfValue"))) + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (indexToRecurtion == 0) {
                        Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber1);
                        displayCardBoard(board, numOfRound, indexToRecurtion + 1, ifNeenAllCards);
                    }
                    else if (indexToRecurtion == 1) {
                        Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber2);
                        displayCardBoard(board, numOfRound, indexToRecurtion + 1, ifNeenAllCards);
                    }
                    else if (indexToRecurtion == 2) {
                        Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber3);
                        displayCardBoard(board, numOfRound, indexToRecurtion + 1, ifNeenAllCards);
                    }
                    else if (indexToRecurtion == 3) {
                        Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber4);
                        displayCardBoard(board, numOfRound, indexToRecurtion + 1, ifNeenAllCards);
                    }
                    else if (indexToRecurtion == 4) {
                        Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber5);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            if (numOfRound == 1) {
                String suit = "" + board.get(indexToRecurtion).get("suit");

                storageRef.child("profilePicturs/pokerCards/" + suit + helpToDisplayCardBoard(Integer.parseInt("" + board.get(indexToRecurtion).get("numOfValue"))) + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (indexToRecurtion == 0) {
                            Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber1);
                            displayCardBoard(board, numOfRound, indexToRecurtion + 1, ifNeenAllCards);
                        } else if (indexToRecurtion == 1) {
                            Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber2);
                            displayCardBoard(board, numOfRound, indexToRecurtion + 1, ifNeenAllCards);
                        } else if (indexToRecurtion == 2) {
                            Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber3);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (numOfRound == 2) {
                String suit = "" + board.get(3).get("suit");

                storageRef.child("profilePicturs/pokerCards/" + suit + helpToDisplayCardBoard(Integer.parseInt("" + board.get(3).get("numOfValue"))) + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber4);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (numOfRound == 3) {
                String suit = "" + board.get(4).get("suit");
                storageRef.child("profilePicturs/pokerCards/" + suit + helpToDisplayCardBoard(Integer.parseInt("" + board.get(4).get("numOfValue"))) + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(pokerGameActivity.this).load(uri).into(cardFlopNumber5);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }//להיעזר כשנגמר ROUND עובד
    public String helpToDisplayCardBoard(int num){
        String numString1="";
        if(num>10){
            if (num==11){
                numString1="j";
            }
            else if (num==12){
                numString1="q";
            }
            else if (num==13){
                numString1="k";
            }
            else if (num==14){
                numString1="a";
            }
        }
        else{
            numString1=""+num;
        }
        return numString1;
    }
    public void doneRoundVisual(){
        btnWhatDidPlayer4Icon.setImageDrawable(null);
        tvRaisedplayer4.setText("");
        btnWhatDidPlayer2Icon.setImageDrawable(null);
        tvRaisedplayer2.setText("");
        btnWhatDidPlayer3Icon.setImageDrawable(null);
        tvRaisedplayer3.setText("");
        btnWhatDidPlayer1Icon.setImageDrawable(null);
        tvRaisedplayer1.setText("");

    }


    public void endGameWithUsers2Players(HashMap<String,Object> PokerTableGame){
        HashMap<String,Object> cardsDetails= (HashMap<String,Object>)PokerTableGame.get("Cards");

        //לכאן ניגש אחד שבודק בשביל כולם
        if (usersArrListInGame.size()==2){
            ArrayList<HashMap> player1HandTemp,player2HandTemp;
            ArrayList<Card> player1Hand=new ArrayList<Card>();
            ArrayList<Card> player2Hand=new ArrayList<Card>();
            ArrayList<HashMap> board=new ArrayList<HashMap>();
            board=(ArrayList<HashMap>) cardsDetails.get("CardsBoard");
            //קיבלנו את הרשימות של הקלפים
            for (int i = 0; i < usersArrListInGame.size(); i++) {
                if (i==0) {
                    player1HandTemp = (ArrayList<HashMap>) cardsDetails.get(usersArrListInGame.get(i).getEmail().replace(".", "*"));
                    for (int j = 0; j < player1HandTemp.size(); j++) {
                        int numCard = Integer.parseInt("" + player1HandTemp.get(j).get("numOfValue"));
                        String suit = "" + player1HandTemp.get(j).get("suit");
                        Card card = new Card(numCard, suit);
                        player1Hand.add(card);
                    }
                    //נוסיף את הלוח
                    for (int j = 0; j < 5; j++) {
                        int numCard = Integer.parseInt("" + board.get(j).get("numOfValue"));
                        String suit = "" + board.get(j).get("suit");
                        Card card = new Card(numCard, suit);
                        player1Hand.add(card);
                    }
                }
                else{
                     player2HandTemp = (ArrayList<HashMap>) cardsDetails.get(usersArrListInGame.get(i).getEmail().replace(".", "*"));
                    for (int j = 0; j < player2HandTemp.size(); j++) {
                        int numCard = Integer.parseInt("" + player2HandTemp.get(j).get("numOfValue"));
                        String suit = "" + player2HandTemp.get(j).get("suit");
                        Card card = new Card(numCard, suit);
                        player2Hand.add(card);
                    }
                    for (int j = 0; j < 5; j++) {
                        int numCard = Integer.parseInt("" + board.get(j).get("numOfValue"));
                        String suit = "" + board.get(j).get("suit");
                        Card card = new Card(numCard, suit);
                        player2Hand.add(card);
                    }
                }
            }
            Collections.sort(player1Hand);
            Collections.sort(player2Hand);
            HashMap<String,Object> resultOfCompare=compareTwoPlayers(player1Hand,player2Hand,new ArrayList<Card>());

            changeResultsWithWinner2Players(resultOfCompare);
        }
        else if (usersArrListInGame.size()==3){

        }
        else{
        }
        // יהיה גם LISTENER לכולם
    }
    public void changeResultsWithWinner2Players(HashMap<String,Object> resultOfCompare){

        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(pokerGameActivity.this, "conect to wifi first", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();
                        HashMap<String, Object> tableDetails = (HashMap<String, Object>) map.get("tableDetails");
                        HashMap<String, Integer> usersDetails = (HashMap<String, Integer>) map.get("usersDetails");
                        if (Integer.parseInt("" + resultOfCompare.get("winnerNum")) == 0) {
                            int numInKupa = Integer.parseInt("" + tableDetails.get("Kupa"));
                            int player1Money = 0, player2Money = 0;
                            for (Map.Entry<String, Integer> entry : usersDetails.entrySet()) {
                                if (entry.getKey().equals(usersArrListInGame.get(0).getEmail().replace(".", "*"))) {
                                    player1Money = Integer.parseInt("" + entry.getValue());
                                } else if (entry.getKey().equals(usersArrListInGame.get(1).getEmail().replace(".", "*"))) {
                                    player2Money = Integer.parseInt("" + entry.getValue());
                                }
                            }
                            player1Money += (int) (numInKupa / 2);
                            player2Money += (int) (numInKupa / 2);
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(usersArrListInGame.get(0).getEmail().replace(".", "*")).setValue(player1Money);
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(usersArrListInGame.get(1).getEmail().replace(".", "*")).setValue(player2Money);
                            endGameAndStartNew(-1);
                        }
                        else if (Integer.parseInt("" + resultOfCompare.get("winnerNum")) == 1) {
                            int numInKupa = Integer.parseInt("" + tableDetails.get("Kupa"));
                            int player1Money = 0;
                            for (Map.Entry<String, Integer> entry : usersDetails.entrySet()) {
                                if (entry.getKey().equals(usersArrListInGame.get(0).getEmail().replace(".", "*"))) {
                                    player1Money = Integer.parseInt("" + entry.getValue());
                                }
                            }
                            player1Money += numInKupa;

                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(usersArrListInGame.get(0).getEmail().replace(".", "*")).setValue(player1Money);
                            //להראות את הקלפים המנצחים לכולם  TODO:

                            endGameAndStartNew(0);
                        }
                        else {
                            int numInKupa = Integer.parseInt("" + tableDetails.get("Kupa"));
                            int player2Money = 0;
                            for (Map.Entry<String, Integer> entry : usersDetails.entrySet()) {
                                if (entry.getKey().equals(usersArrListInGame.get(1).getEmail().replace(".", "*"))) {
                                    player2Money = Integer.parseInt("" + entry.getValue());
                                }
                            }
                            player2Money += numInKupa;

                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").child(usersArrListInGame.get(1).getEmail().replace(".", "*")).setValue(player2Money);
                            //להראות את הקלפים המנצחים לכולם  TODO:
                            endGameAndStartNew(1);
                        }

                    } catch (Exception e) {
                        Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



    public HashMap<String, Object> compareTwoPlayers(ArrayList < Card > hand1, ArrayList < Card > hand2, ArrayList < Card > winnerHand){
        ArrayList<Card> winnerHand1 = new ArrayList<Card>(), winnerHand2 = new ArrayList<Card>();
        int num1 = checkStrongestHand(hand1,winnerHand1);
        int num2 = checkStrongestHand(hand2,winnerHand2);
        HashMap<String, Object> returnHash = new HashMap<String, Object>();
        if (num1 < num2) {
            returnHash.put("winnerHand", winnerHand);
            returnHash.put("winnerNum", 1);
            return returnHash;
        }
        else if (num1 > num2) {
            returnHash.put("winnerHand", winnerHand);
            returnHash.put("winnerNum", 2);
            return returnHash;
        }
        else {
            for (int i = 0; i < winnerHand1.size() ; i++) {
                if (winnerHand1.get(i).getNumOfValue() > winnerHand2.get(i).getNumOfValue()) {
                    winnerHand = winnerHand1;
                    returnHash.put("winnerHand", winnerHand);
                    returnHash.put("winnerNum", 1);
                    return returnHash;
                }
                if (winnerHand1.get(i).getNumOfValue() < winnerHand2.get(i).getNumOfValue()) {
                    winnerHand = winnerHand2;
                    returnHash.put("winnerHand", winnerHand);
                    returnHash.put("winnerNum", 2);
                    return returnHash;
                }
            }
        }
        returnHash.put("winnerHand", winnerHand);
        returnHash.put("winnerNum", 0);
        return returnHash;

    }
    // בודקת את היד מחזירה 1- סטרייט פלאש, 2- רבעייה, 3- פול האוס, 4- צבע, 5- רצף, 6- שלשה, 7- זוגיים, 8- זוג, 9- קלף גבוה
    private int checkStrongestHand (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        ArrayList<Card> hand2 = new ArrayList<Card>();
        for (int i = 0; i < hand.size(); i++) {
            hand2.add(hand.get(i));
        }
        int num = 0;
        if (checkStraightFlush(hand, winnerHand).equals("true")){ return 1;} // סטרייט פלאש
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++) {
            hand.add(hand2.get(i));
        }
        if (checkFourOfAKind(hand, winnerHand).equals("true")) {
            winnerHand.add(highCard(hand));
            return 2;
        } // רבעייה
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++) {hand.add(hand2.get(i));}
        if (checkFullHouse(hand, winnerHand).equals("true")) return 3; //פול האוס
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++) {hand.add(hand2.get(i));}
        if (checkFlush(hand, winnerHand).equals("true")) {
            while (winnerHand.size() > 5) winnerHand.remove(0);
            return 4;
        } // צבע
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++) {hand.add(hand2.get(i));}
        if (checkStraight(hand, winnerHand).equals("true")) return 5; // רצף
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++) {hand.add(hand2.get(i));}
        num = checkThreeOfAKind(hand, winnerHand);
        if (num != 0) {
            winnerHand.add(highCard(hand));
            winnerHand.add(highCard(hand));
            return 6;
        } // שלשה
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++){ hand.add(hand2.get(i));}
        if (checkTwoPair(hand, winnerHand).equals("true")) {
            winnerHand.add(highCard(hand));
            return 7;
        } // זוגיים
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++) {hand.add(hand2.get(i));}
        num = checkPair(hand, winnerHand);
        if (num != 0) {
            while (winnerHand.size() < 5) winnerHand.add(highCard(hand));
            return 8;
        } // זוג
        winnerHand.clear();
        hand.clear();
        for (int i = 0; i < hand2.size(); i++) {hand.add(hand2.get(i));}
        while (winnerHand.size() < 5) winnerHand.add(highCard(hand));
        return 9;
    }
    // בודק אם קיים סטרייט פלאש
    private String checkStraightFlush (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        ArrayList<Card> helper = new ArrayList<Card>();
        checkFlush(hand, helper);
        return checkStraight(helper, winnerHand);
    }
    // בודק אם קיימת רביעייה
    private String checkFourOfAKind (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        int num1 = checkPair(hand, winnerHand), num2 = checkPair(hand, winnerHand);
        while (num1 != 0) {
            if (num1 != 0 && num2 != 0 && num2 == num1) return "true";
            num1 = checkPair(hand, winnerHand);
        }
        return "false";
    }
    // בודק אם קיים פול האוס
    private String checkFullHouse (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        int num1 = checkThreeOfAKind(hand, winnerHand), num2 = checkPair(hand, winnerHand);
        if (num1 != 0) {
            if (num2 != 0) {
                return "true";
            }
        }
        return "false";
    }
    // בודק אם קיים צבע
    private String checkFlush (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        ArrayList<Card> p = new ArrayList<Card>();
        ArrayList<Card> s = new ArrayList<Card>();
        ArrayList<Card> k = new ArrayList<Card>();
        ArrayList<Card> l = new ArrayList<Card>();
        int maxP = 0, maxS = 0, maxK = 0, maxL = 0;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getSuit().equals("p")) {
                p.add(hand.get(i));
                maxP = Math.max(maxP, hand.get(i).getNumOfValue());
            } else if (hand.get(i).getSuit().equals("s")) {
                s.add(hand.get(i));
                maxS = Math.max(maxS, hand.get(i).getNumOfValue());
            } else if (hand.get(i).getSuit().equals("k")) {
                k.add(hand.get(i));
                maxK = Math.max(maxK, hand.get(i).getNumOfValue());
            } else if (hand.get(i).getSuit().equals("p")) {
                l.add(hand.get(i));
                maxL = Math.max(maxL, hand.get(i).getNumOfValue());
            }
        }
        if (p.size() > 4) {
            for (int i = 0; i < p.size(); i++) winnerHand.add(p.get(i));
            return "true";
        } else if (s.size() > 4) {
            for (int i = 0; i < s.size(); i++) winnerHand.add(s.get(i));
            return "true";
        } else if (k.size() > 4) {
            for (int i = 0; i < k.size(); i++) winnerHand.add(k.get(i));
            return "true";
        } else if (l.size() > 4) {
            for (int i = 0; i < l.size(); i++) winnerHand.add(l.get(i));
            return "true";
        }
        return "false";
    }
    // בודק אם קיים רצף
    private String checkStraight (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        if (!hand.isEmpty()) {
            for (int i = hand.size() - 1; i > 0; i--) {
                if (hand.get(i).getNumOfValue() - hand.get(i - 1).getNumOfValue() == 1) {
                    winnerHand.add(0, hand.get(i));
                    if (winnerHand.size() == 4) {
                        winnerHand.add(0, hand.get(i - 1));
                        return "true";
                    }
                } else winnerHand.clear();
            }
            if (hand.get(hand.size() - 1).getNumOfValue() == 14 && hand.get(0).getNumOfValue() == 2) {
                if (winnerHand.size() == 3) {
                    winnerHand.add(0, hand.get(0));
                    winnerHand.add(0, hand.get(hand.size() - 1));
                    return "true";
                }
            }
        }
        return "false";
    }
    // בודק אם קיימת שלשה ומחזירה את המספר אם כן ו0 אם לא
    private int checkThreeOfAKind (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        int num1 = -1;
        ArrayList<Card> helper = new ArrayList<Card>();
        while (num1 != 0) {
            num1 = checkPair(hand, winnerHand);
            for (int i = hand.size() - 1; i >= 0; i--) {
                if (num1 == hand.get(i).getNumOfValue()) {
                    for (int j = helper.size() - 1; j >= 0; j--) hand.add(helper.get(j));
                    winnerHand.add(hand.get(i));
                    hand.remove(i);
                    return num1;
                }
                for (int j = 0; j < winnerHand.size(); j++) helper.add(winnerHand.get(j));
                winnerHand.clear();
            }
        }
        return 0;
    }
    // בודק אם קיימים זוגיים
    private String checkTwoPair (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        int num1 = checkPair(hand, winnerHand), num2 = checkPair(hand, winnerHand);
        if (num1 != 0 && num2 != 0) return "true";
        return "false";
    }
    // בודק אם קיים זוג ומחזיר את המספר אם לא קיים אז מחזיר 0
    private int checkPair (ArrayList < Card > hand, ArrayList < Card > winnerHand){
        for (int i = hand.size() - 1; i > 0; i--) {
            if (hand.get(i).getNumOfValue() == hand.get(i - 1).getNumOfValue()) {
                int num = hand.get(i).getNumOfValue();
                winnerHand.add(hand.get(i));
                hand.remove(i);
                winnerHand.add(hand.get(i - 1));
                hand.remove(i - 1);
                return num;
            }
        }
        return 0;
    }
    //מחזיר את הקלף הגבוה
     private Card highCard (ArrayList < Card > hand){
        return hand.remove(hand.size() - 1);
    }




    public void endGameWhenFolds(HashMap<String,Object> PokerTableGame){
       HashMap<String,Integer> usersDetails=(HashMap<String,Integer>) PokerTableGame.get("usersDetails");
        HashMap<String,Object> tableDetails=(HashMap<String,Object>) PokerTableGame.get("tableDetails");
        HashMap<String,String> activeActions=(HashMap<String,String>) PokerTableGame.get("activeActions");
        int numKupa= Integer.parseInt(""+tableDetails.get("Kupa"));
        int index=0,numPlayer=0;
        for (Map.Entry<String,String> entry:activeActions.entrySet()){
            if (!entry.getValue().contains("fold")){
                int moneyPlayer=Integer.parseInt(""+usersDetails.get(entry.getKey()));
                usersDetails.put(entry.getKey(),numKupa+moneyPlayer);
                numPlayer=index;
            }
            index++;
        }
        int numPlayerReal=numPlayer;
        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").setValue(usersDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                endGameAndStartNew(numPlayerReal);
            }
        });

    }

    public void timerIsDoneMyTurn(){

    }


    public void endGameAndStartNew(int numWinner) {
        //צריך לשנות את כל הDATABASE
        try {
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("player" + (myPlace - 1) + "done").removeEventListener(listener);
        } catch (Exception e) {}
        try {
            dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("turn").removeEventListener(listenerToChangeTurn);
        } catch (Exception e) {}
        try {
            dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("Cards").child("CardsBoard").removeEventListener(listenerToChangeRound);
        } catch (Exception e) {}
        try {
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endGame").removeEventListener(listenerToEndGame);
        } catch (Exception e) {}
        /*
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("tableDetails").child("turn").removeEventListener(listenerToChangeTurn);
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+tableNum).child("Cards").child("CardsBoard").removeEventListener(listenerToChangeRound);
        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endGame").removeEventListener(listenerToEndGame);
         */
        //הורדנו LISTENERs
        checkIfGameIsDoneAndChangeDataBase(numWinner);
    }
    public void listenerToEndGame(){
        listenerToEndGame=dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endGame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null){
                    dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endGame").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endGame").removeValue(); //TODO כשבודק עם עוד מכשירים אז לבדוק אם בסדר ולא מקריס לשאר
                            int numGames=Integer.parseInt(""+snapshot.getValue());
                            if (numGames==numGamesPlayedCounter) {
                                numGamesPlayedCounter++;
                                CountDownTimer temp = new CountDownTimer(2000, 2000) {
                                    @Override
                                    public void onTick(long l) {

                                    }

                                    @Override
                                    public void onFinish() {

                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        downoadrealtime();
                                    }
                                }.start();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //לאחר סיום בודק אם נגמר סופית ומישהו הפסיד
    public void checkIfGameIsDoneAndChangeDataBase(int numWinner){

        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue()!=null && task.isSuccessful()){
                    int numPlayersLoseInGame=0;
                    HashMap<String,Object> allTable=(HashMap <String,Object>)task.getResult().getValue();
                    HashMap<String,Integer> usersDetails=(HashMap <String,Integer>)allTable.get("usersDetails");
                    HashMap<String,String> usersActions=(HashMap <String,String>)allTable.get("activeActions");
                    for (Map.Entry<String,Integer> entry : usersDetails.entrySet()){
                        int numMoney=Integer.parseInt(""+entry.getValue());
                        if (numMoney==0){
                            numPlayersLoseInGame++;
                        }
                    }
                    if (usersArrListInGame.size()-numPlayersLoseInGame>1){
                        //כשהמשחק נמשך
                        if (numPlayersLoseInGame>0) {
                            for (Map.Entry<String, Integer> entry : usersDetails.entrySet()) {
                                int numMoney = Integer.parseInt("" + entry.getKey());
                                if (numMoney == 0) {
                                    usersActions.remove(entry.getKey());
                                    usersDetails.remove(entry.getKey());
                                    makeToastToEveryOne(entry.getKey().replace("*", ".") + " loses the game");
                                }
                            }
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").setValue(usersActions);//אמור להסתיים עד שהבא מסתיים
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").setValue(usersDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //change data base
                                        {
                                            HashMap<String, ArrayList<Card>> cards = new HashMap<String, ArrayList<Card>>();
                                            ArrayList<Card> arrAllCards = new ArrayList<Card>();
                                            for (int i = 2; i < 15; i++) {
                                                Card card = new Card(i, "k");
                                                arrAllCards.add(card);
                                            }
                                            for (int i = 2; i < 15; i++) {
                                                Card card = new Card(i, "l");
                                                arrAllCards.add(card);
                                            }
                                            for (int i = 2; i < 15; i++) {
                                                Card card = new Card(i, "p");
                                                arrAllCards.add(card);
                                            }
                                            for (int i = 2; i < 15; i++) {
                                                Card card = new Card(i, "s");
                                                arrAllCards.add(card);
                                            }
                                            cards.put("cardsInDeck", arrAllCards);
                                            //////
                                            HashMap<String, String> activegameUsersActiveActions = new HashMap<String, String>();
                                            ArrayList<Card> list = new ArrayList<Card>();
                                            Card card = new Card(-1, "p");
                                            list.add(card);
                                            for (int i = 0; i < usersArrListInGame.size(); i++) {
                                                activegameUsersActiveActions.put("" + usersArrListInGame.get(i).getEmail().replace(".", "*"), "wait");
                                                cards.put(usersArrListInGame.get(i).getEmail().replace(".", "*"), list);
                                            }

                                            cards.put("flopCards", list);
                                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").setValue(cards);
                                            //////activeDitails
                                            HashMap<String, Integer> activegameDetails = new HashMap<String, Integer>();
                                            activegameDetails.put("Kupa", 0);
                                            activegameDetails.put("numInPot", numInPot);
                                            activegameDetails.put("roundsEnded", 0);
                                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").setValue(activegameDetails);//עדכנו את השולחן החדש
                                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").setValue(activegameUsersActiveActions);
                                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endGame").setValue(numGamesPlayedCounter);
                                            numGamesPlayedCounter++;
                                            if (numWinner != -1) {
                                                makeToastToEveryOne( usersArrListInGame.get(numWinner).getEmail() + " is the winner");
                                            } else {
                                                makeToastToEveryOne("there was a tie");
                                            }
                                            CountDownTimer temp = new CountDownTimer(1000, 1000) {
                                                @Override
                                                public void onTick(long l) {
                                                    if (l == 100) {
                                                    }
                                                    //להציג את הקלפים

                                                }

                                                @Override
                                                public void onFinish() {
                                                    downoadrealtime();
                                                }
                                            }.start();
                                            //TODO להמשיך פה
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            HashMap<String, ArrayList<Card>> cards = new HashMap<String, ArrayList<Card>>();
                            ArrayList<Card> arrAllCards = new ArrayList<Card>();
                            for (int i = 2; i < 15; i++) {
                                Card card = new Card(i, "k");
                                arrAllCards.add(card);
                            }
                            for (int i = 2; i < 15; i++) {
                                Card card = new Card(i, "l");
                                arrAllCards.add(card);
                            }
                            for (int i = 2; i < 15; i++) {
                                Card card = new Card(i, "p");
                                arrAllCards.add(card);
                            }
                            for (int i = 2; i < 15; i++) {
                                Card card = new Card(i, "s");
                                arrAllCards.add(card);
                            }
                            cards.put("cardsInDeck", arrAllCards);
                            //////
                            HashMap<String, String> activegameUsersActiveActions = new HashMap<String, String>();
                            ArrayList<Card> list = new ArrayList<Card>();
                            Card card = new Card(-1, "p");
                            list.add(card);
                            for (int i = 0; i < usersArrListInGame.size(); i++) {
                                activegameUsersActiveActions.put("" + usersArrListInGame.get(i).getEmail().replace(".", "*"), "wait");
                                cards.put(usersArrListInGame.get(i).getEmail().replace(".", "*"), list);
                            }
                            cards.put("flopCards", list);
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").setValue(cards);
                            //////activeDitails
                            HashMap<String, Integer> activegameDetails = new HashMap<String, Integer>();
                            activegameDetails.put("Kupa", 0);
                            activegameDetails.put("numInPot", numInPot);
                            activegameDetails.put("roundsEnded", 0);
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").setValue(activegameDetails);//עדכנו את השולחן החדש
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").setValue(activegameUsersActiveActions);
                            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("tableDetails").child("endGame").setValue(numGamesPlayedCounter);
                            numGamesPlayedCounter++;
                            if (numWinner != -1) {
                                makeToastToEveryOne( usersArrListInGame.get(numWinner).getEmail() + " is the winner");
                                Toast.makeText(pokerGameActivity.this,usersArrListInGame.get(numWinner).getEmail() + " is the winner",Toast.LENGTH_SHORT).show();
                            } else {
                                makeToastToEveryOne("there was a tie");
                                Toast.makeText(pokerGameActivity.this,"there was a tie",Toast.LENGTH_SHORT).show();

                            }
                            CountDownTimer temp = new CountDownTimer(1000, 1000) {
                                @Override
                                public void onTick(long l) {
                                    if (l == 100) {
                                    }
                                    //להציג את הקלפים
                                }
                                @Override
                                public void onFinish() {
                                    downoadrealtime();
                                }
                            }.start();
                            //TODO להמשיך פה

                        }
                    }
                    else{
                        //כשהמשחק נגמר
                        for (Map.Entry<String,Integer> entry : usersDetails.entrySet()) {
                            int numMoney = Integer.parseInt("" + entry.getValue());
                            if (numMoney == 0) {
                                usersActions.remove(entry.getKey());
                                usersDetails.remove(entry.getKey());
                                makeToastToEveryOne(entry.getKey().replace("*", ".") + " loses the game");
                            }
                        }
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("activeActions").setValue(usersActions);//אמור להסתיים עד שהבא מסתיים
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("usersDetails").setValue(usersDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                   // endAllGameAndDeclareWinner
                                    for (Map.Entry<String,Integer> entry : usersDetails.entrySet()) {//ירוץ פעם אחת רק למנצח
                                        makeToastToEveryOne(entry.getKey().replace("*",".") +" is the winner");
                                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("endAllGame").setValue(entry.getKey().replace("*","."));
                                    }
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
    //סיימתי ביום שישי שלוש בבוקר לבדוק אם הכל מעודכן צריך להריץ הכל


    public void makeToastToEveryOne(String toast){
        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("ToastToEveryOne").setValue(toast);
    }
    public void toastToEveryOneListener(){
        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("ToastToEveryOne").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Toast.makeText(pokerGameActivity.this,""+snapshot.getValue(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void  setListenerToEndAllGame(){
         listenerEndAllGame = dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("endAllGame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //שם LISTENER לכל שינוי בכסף של השחקנים
                    String winner=""+dataSnapshot.getValue().toString();
                    if (winner.replace("*",".").equals(mAuth.getCurrentUser().getEmail())){
                        Toast.makeText(pokerGameActivity.this,"congratulations you won",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(pokerGameActivity.this,"you lost better luck next time",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("endAllGame").removeEventListener(listenerEndAllGame);
                    }catch (Exception e){}
                    Intent it=new Intent(pokerGameActivity.this,MainActivity.class);
                    startActivity(it);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(pokerGameActivity.this, "eror", Toast.LENGTH_SHORT).show();
            }
        });

    }
}