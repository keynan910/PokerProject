package com.example.quizproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class pokerGameFindTable extends AppCompatActivity implements View.OnClickListener {

    //openTable
    DatabaseReference dataBaseRef;
    TextView tvTableStatus,tvTableCode,tvListparticipants;
    Button btnCreateTable,btnFindTable,btnOpenTable,btnCancelTable,btnStartGame;
    ListView lvJoinTable;
    CardView cardViewCreateTable;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    JoinTableAdapter JoinTableAdapter;
    ListView lvCustomLayoutUsersInGame;
    //FindTable
    TextView tvTableFindStatus,tvTableNumber,tvTableOwner;
    EditText etWriteTableCode;
    Button btnSearchTable,btnAcceptTable;
    CardView cardViewFindTable;
    ConstraintLayout FindTableTableProperties;
    //waiting dialog
    //code
    boolean tableOpen=false;
    Map<String, Object> documentUsersInFireBase;
    Intent startGame;
    CountDownTimer timerToGetBack;
    int check=0;
    //dialog stats;
    Dialog waitingDialog;
    Button btnCancelWaitingDialog;
    TextView tvTableDetailsDialog,tvTableDetailsNumberDialog;
    ArrayList<User> usersArrInMyTable;
    //dialog money
    Dialog dialogMoney;
    TextView tvMoneyUserGetIn,tvMoneyEveryPot;
    SeekBar seekBarMoneyUsers,seekBarMoneyPot;
    Button btnDoneChooseMoney;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_game_find_table);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//עושה למשתמש בLANDSCAPE

        btnCreateTable=findViewById(R.id.btnCreateTable);
        btnFindTable=findViewById(R.id.btnFindTable);
        tvTableStatus=findViewById(R.id.tvTableStatus);
        tvTableCode=findViewById(R.id.tvTableCode);
        lvJoinTable=findViewById(R.id.lvJoinTable);
        btnOpenTable=findViewById(R.id.btnOpenTable);
        btnStartGame=findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(this);
        cardViewCreateTable=findViewById(R.id.cardViewCreateTable);
        btnCancelTable=findViewById(R.id.btnCancelTable);
        btnCancelTable.setOnClickListener(this);
        tvListparticipants=findViewById(R.id.tvListparticipants);
        //FindTable
        tvTableFindStatus=findViewById(R.id.tvTableFindStatus);
        tvTableNumber=findViewById(R.id.tvTableNumber);
        tvTableOwner=findViewById(R.id.tvTableOwner);
        etWriteTableCode=findViewById(R.id.etWriteTableCode);
        btnAcceptTable=findViewById(R.id.btnAcceptTable);
        btnSearchTable=findViewById(R.id.btnSearchTable);
        cardViewFindTable=findViewById(R.id.cardViewFindTable);
        FindTableTableProperties=findViewById(R.id.FindTableTableProperties);
        btnAcceptTable.setOnClickListener(this);
        btnSearchTable.setOnClickListener(this);


        btnOpenTable.setOnClickListener(this);
        btnCreateTable.setOnClickListener(this);
        btnFindTable.setOnClickListener(this);
        dataBaseRef = FirebaseDatabase.getInstance(getString(R.string.fireBaseGetInstance)).getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //at First create button is on
        tvTableCode.setText("");
        tvTableStatus.setText("");
        btnOpenTable.setVisibility(View.VISIBLE);
        cardViewCreateTable.setVisibility(View.VISIBLE);
        cardViewFindTable.setVisibility(View.GONE);
        btnCreateTable.setAlpha((float)0.5);
        btnFindTable.setAlpha((float)1);
        btnCreateTable.setClickable(false);
        btnFindTable.setClickable(true);
        btnCancelTable.setVisibility(View.GONE);
        btnStartGame.setVisibility(View.GONE);
        tvListparticipants.setVisibility(View.GONE);
        etWriteTableCode.setPaintFlags(etWriteTableCode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        downloadFireStoreInBackground();//use fire base with the transfer data to next page
    }


    @Override
    public void onClick(View view) {
        //find table
        if (view==btnFindTable){//find table
            returnToFindPage();
        }
        else if (view==btnSearchTable){
            InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(etWriteTableCode.getWindowToken(), 0);//הורדת המקלדת
            String numOfTableToSearch=etWriteTableCode.getText().toString();
            if (!numOfTableToSearch.equals("")){
                try {
                    int numOfTable = Integer.parseInt(numOfTableToSearch);
                    dataBaseRef.child("pokerTableGames").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//לקחנו את כל המסמך
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(pokerGameFindTable.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                try {//בודקים אם קיים שולחן כזה ומציגים אותו
                                    HashMap<String, Object> resultPokerTableGames = (HashMap<String, Object>) task.getResult().getValue();
                                    if (task.getResult().getValue() != null) {
                                        HashMap<String, Object> PokerTableGames = (HashMap<String, Object>) resultPokerTableGames.get("pokerTables");
                                        if (PokerTableGames.containsKey("" + numOfTable)){
                                            ArrayList<String> usersList = (ArrayList<String>) PokerTableGames.get("" + numOfTable);
                                            if (usersList.size()==4){
                                                tvTableFindStatus.setText("The table is full");
                                            }
                                            else{
                                                FindTableTableProperties.setVisibility(View.VISIBLE);
                                                tvTableOwner.setText("Owner: "+usersList.get(0));
                                                tvTableNumber.setText("Table number: "+numOfTable);
                                                tvTableFindStatus.setText("");
                                            }
                                        }
                                        else{
                                            FindTableTableProperties.setVisibility(View.GONE);
                                            tvTableFindStatus.setText("There is no table try again");
                                        }
                                    }
                                    else{
                                        FindTableTableProperties.setVisibility(View.GONE);
                                        tvTableFindStatus.setText("There is no table try again");
                                    }
                                }
                                catch (Exception e){
                                    Toast.makeText(pokerGameFindTable.this,"eror",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                catch (Exception e){
                    tvTableFindStatus.setText("You need to enter table code");
                }
            }
            else{
                tvTableFindStatus.setText("You need to enter table code");
            }
        }
        else if (btnAcceptTable==view){
            String TableNumberTv=tvTableNumber.getText().toString();
            String[] TableNumberarr =TableNumberTv.split("Table number: ");
            int tablenum=Integer.parseInt(""+TableNumberarr[1]);
            dataBaseRef.child("pokerTableGames").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//לקחנו את כל המסמך
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(pokerGameFindTable.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {//בודקים אם קיים שולחן זה עדיין available
                            HashMap<String, Object> resultPokerTableGames = (HashMap<String, Object>) task.getResult().getValue();
                            if (task.getResult().getValue() != null) {
                                HashMap<String, Object> PokerTableGames = (HashMap<String, Object>) resultPokerTableGames.get("pokerTables");
                                if (PokerTableGames.containsKey("" + tablenum)) {
                                    ArrayList<String> usersList = (ArrayList<String>) PokerTableGames.get("" + tablenum);
                                    if (usersList.size() == 4) {
                                        tvTableFindStatus.setText("The table is full");
                                    } else {
                                        deleteTableToVerifyAndEnterNew(tablenum);//פעולה שגם מוחקת אם לאיש שנכנס יש כבר קישורים לשולחן וגם מוסיפה אותו לחדש
                                        createWaitingScreen(tablenum,usersList.get(0));
                                    }
                                }

                                else{
                                    tvTableFindStatus.setText("Table is not available try again");
                                    returnToFindPage();
                                }
                            }
                            else{
                                tvTableFindStatus.setText("Table is not available try again");
                                returnToFindPage();
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(pokerGameFindTable.this,"eror",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        else if (view==btnCancelWaitingDialog){
            waitingDialog.cancel();
            deleteTable();
        }
        //create table
        else if(view==btnCreateTable){
            tvTableCode.setText("");
            tvTableStatus.setText("");
            btnOpenTable.setVisibility(View.VISIBLE);
            cardViewCreateTable.setVisibility(View.VISIBLE);
            cardViewFindTable.setVisibility(View.GONE);
            btnCreateTable.setAlpha((float)0.5);
            btnFindTable.setAlpha((float)1);
            btnCreateTable.setClickable(false);
            btnFindTable.setClickable(true);
            btnCancelTable.setVisibility(View.GONE);
            btnStartGame.setVisibility(View.GONE);
            tvListparticipants.setVisibility(View.GONE);
        }
        else if (view==btnOpenTable){
            btnOpenTable.setClickable(false);
            btnStartGame.setVisibility(View.VISIBLE);
            btnOpenTable.setVisibility(View.GONE);
            btnCancelTable.setVisibility(View.VISIBLE);
            tvListparticipants.setVisibility(View.VISIBLE);
            tableOpen=true;
            // Read from the database
            dataBaseRef.child("pokerTableGames").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//לקחנו את כל המסמך
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(pokerGameFindTable.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //  Toast.makeText( pokerGameFindTable.this, String.valueOf(task.getResult().getValue()),Toast.LENGTH_SHORT).show();
                        HashMap<String,Object> resultPokerTableGames = (HashMap<String, Object>) task.getResult().getValue();
                        if (task.getResult().getValue()!=null) {
                            addTableGame((HashMap<String, Object>) resultPokerTableGames.get("pokerTables"), (HashMap<String, Object>) resultPokerTableGames.get("usersToTables"));//הבאנו פעם אחת את הטבלה של השולחנות ופעם של משתמשים
                            downloadFireStoreInBackground();
                        }
                        else{
                            addTableGame(null,null);
                            downloadFireStoreInBackground();
                        }
                        //       }
                    }
                }
            });
        }
        else if (btnCancelTable==view){
            if (tableOpen){
                tableOpen=false;
                tvTableCode.setText("");
                tvTableStatus.setText("");
                btnOpenTable.setVisibility(View.VISIBLE);
                btnCancelTable.setVisibility(View.GONE);
                btnOpenTable.setClickable(true);
                btnStartGame.setVisibility(View.GONE);
                tvListparticipants.setVisibility(View.GONE);
                deleteTable();
            }
        }
        else if(btnStartGame==view){//להמשיך מכאן
            if (usersArrInMyTable==null || usersArrInMyTable.size()<1){
                Toast.makeText(pokerGameFindTable.this,"you need at least 2 players to play",Toast.LENGTH_SHORT).show();
            }
            else{
                dialogMoney= new Dialog(pokerGameFindTable.this);
                dialogMoney.setContentView(R.layout.dialogaskmoney);
                dialogMoney.setCancelable(false);
                tvMoneyUserGetIn = dialogMoney.findViewById(R.id.tvMoneyUserGetIn);
                tvMoneyEveryPot = dialogMoney.findViewById(R.id.tvMoneyEveryPot);
                seekBarMoneyUsers = dialogMoney.findViewById(R.id.seekBarMoneyUsers);
                seekBarMoneyPot = dialogMoney.findViewById(R.id.seekBarMoneyPot);
                btnDoneChooseMoney = dialogMoney.findViewById(R.id.btnDoneChooseMoney);
                btnDoneChooseMoney.setOnClickListener(this);
                setSeekbars(seekBarMoneyUsers,tvMoneyUserGetIn,seekBarMoneyPot,tvMoneyEveryPot);
                dialogMoney.show();
            }
        }
        else if(btnDoneChooseMoney==view) {
            String TableNumberTv = tvTableCode.getText().toString();
            String[] TableNumberarr = TableNumberTv.split("The table code is: ");
            int tablenum = Integer.parseInt("" + TableNumberarr[1]);//לקחנו את מספר השולחן
            String numToEachUser, numInPot;
            numToEachUser = tvMoneyUserGetIn.getText().toString().replace("$", "");
            numInPot = tvMoneyEveryPot.getText().toString().replace("$", "");
            ///////////
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
            HashMap<String, Integer> activegameUsers = new HashMap<String, Integer>();
            ArrayList<Card> list = new ArrayList<Card>();
            Card card = new Card(-1, "p");
            list.add(card);
            for (int i = 0; i < usersArrInMyTable.size(); i++) {
                activegameUsers.put("" + usersArrInMyTable.get(i).getEmail().replace(".", "*"), Integer.parseInt(numToEachUser));
                activegameUsersActiveActions.put("" + usersArrInMyTable.get(i).getEmail().replace(".", "*"), "wait");
                cards.put(usersArrInMyTable.get(i).getEmail().replace(".", "*"), list);
            }
            activegameUsers.put("" + user.getEmail().replace(".", "*"), Integer.parseInt(numToEachUser));
            cards.put(user.getEmail().replace(".", "*"), list);
            cards.put("flopCards", list);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tablenum).child("Cards").setValue(cards);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tablenum).child("usersDetails").setValue(activegameUsers);
            //////activeDitails
            HashMap<String, Integer> activegameDetails = new HashMap<String, Integer>();
            activegameDetails.put("Kupa", 0);
            activegameDetails.put("numInPot", Integer.parseInt(numInPot));
            activegameDetails.put("roundsEnded", 0);
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tablenum).child("tableDetails").setValue(activegameDetails);//עדכנו את השולחן החדש
            activegameUsersActiveActions.put("" + user.getEmail().replace(".", "*"), "wait");
            dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tablenum).child("activeActions").setValue(activegameUsersActiveActions);
            Intent startGame = new Intent(pokerGameFindTable.this, pokerGameActivity.class);
            startGame.putExtra("tableNumber", tablenum);
            startGame.putExtra("numOfPlayers", usersArrInMyTable.size());
            dialogMoney.dismiss(); //לא להעביר משתמשים כי למי שלא אדמין אין גישה
            startActivity(startGame);


        }
    }




    public void addTableGame(HashMap<String,Object> resultPokerTableGames,HashMap<String,Object> resultPokerUsersToTables) {
        int num = 1000 + (int) (Math.random() * 1000);//1000-2000
        if (resultPokerTableGames != null) {
            boolean isKeyPresent = resultPokerTableGames.containsKey("" + num);
            if (isKeyPresent) {
                addTableGame(resultPokerTableGames, resultPokerUsersToTables);
            } else {
                //check if user is with a game if it is destroy its game
                try {
                    int checkIfInTable = Integer.parseInt("" + resultPokerUsersToTables.get(user.getEmail().replace(".", "*")));
                    ArrayList<String> usersInTableList = (ArrayList<String>) resultPokerTableGames.get("" + checkIfInTable);     //TODO חשוב מאוד לאפליקציה להמשך לעשות שכל הזמן בודק אם מישהו יצא כי עשיתי שאם מחפש שולחן אחר אז רק מוציא אותו מהרשימה של השולחנות
                    usersInTableList.remove(user.getEmail());
                    dataBaseRef.child("pokerTableGames").child("pokerTables").child("" + checkIfInTable).setValue(usersInTableList);
                } catch (Exception e) {

                }
                ArrayList<String> usersList = new ArrayList<String>();
                usersList.add(user.getEmail());
                dataBaseRef.child("pokerTableGames").child("pokerTables").child("" + num).setValue(usersList);//אחרי ששינינו את השולחנון והוספנו שולחן ברקע נשייך את המשתמש

                String appropriateEmail = user.getEmail().replace(".", "*");
                dataBaseRef.child("pokerTableGames").child("usersToTables").child(appropriateEmail).setValue(num);
            }
        } else {
            ArrayList<String> usersList = new ArrayList<String>();
            usersList.add(user.getEmail());
            dataBaseRef.child("pokerTableGames").child("pokerTables").child("" + num).setValue(usersList);//אחרי ששינינו את השולחנון והוספנו שולחן ברקע נשייך את המשתמש

            String appropriateEmail = user.getEmail().replace(".", "*");
            dataBaseRef.child("pokerTableGames").child("usersToTables").child(appropriateEmail).setValue(num);
        }
        tvTableCode.setText("The table code is: "+num);//משנה את הכיתוב באפליקציה
        tvTableStatus.setText("the table status is active");
        tableOpen=true;
        listenerToJoinPeople(num);
    }
    public void deleteTable(){
        dataBaseRef.child("pokerTableGames").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//לקחנו את כל המסמך
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(pokerGameFindTable.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        HashMap<String, Object> resultPokerTableGames = (HashMap<String, Object>) task.getResult().getValue();
                        if (task.getResult().getValue() != null) {
                            HashMap<String, Object> PokerTableGames = (HashMap<String, Object>) resultPokerTableGames.get("pokerTables");
                            HashMap<String, Object> resultPokerUsersToTables = (HashMap<String, Object>) resultPokerTableGames.get("usersToTables");
                            if(resultPokerUsersToTables.get("" + user.getEmail().replace(".", "*"))!=null) {
                                int numTable = Integer.parseInt("" + resultPokerUsersToTables.get("" + user.getEmail().replace(".", "*")));
                                ArrayList<String> usersList = (ArrayList<String>) PokerTableGames.get("" + numTable);
                                int numOfUser = usersList.indexOf(user.getEmail());
                                if (numOfUser == 0) {//אם מי שמוחקים אותו הוא המנהל של השולחן
                                    for (int i = 0; i < usersList.size(); i++) {
                                        resultPokerUsersToTables.remove("" + usersList.get(i).replace(".", "*"));
                                    }
                                    dataBaseRef.child("pokerTableGames").child("usersToTables").setValue(resultPokerUsersToTables);
                                    PokerTableGames.remove("" + numTable);
                                    dataBaseRef.child("pokerTableGames").child("pokerTables").setValue(PokerTableGames);//אחרי ששינינו את השולחנון והוספנו שולחן ברקע נשייך את המשתמש
                                } else {//לא המנהל אז מוציאים רק אותו
                                    resultPokerUsersToTables.remove("" + user.getEmail().replace(".", "*"));
                                    dataBaseRef.child("pokerTableGames").child("usersToTables").setValue(resultPokerUsersToTables);
                                    usersList.remove(user.getEmail());
                                    dataBaseRef.child("pokerTableGames").child("pokerTables").child("" + numTable).setValue(usersList);

                                }
                            }
                        }
                    }
                    catch (Exception e){
                        //Toast.makeText(pokerGameFindTable.this,"eror",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }//  מוחק ובודק אם מנהל או לא
    public void deleteTableToVerifyAndEnterNew(int tablenum){
        dataBaseRef.child("pokerTableGames").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//לקחנו את כל המסמך
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(pokerGameFindTable.this,"conect to wifi first",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        HashMap<String, Object> resultPokerTableGames = (HashMap<String, Object>) task.getResult().getValue();
                        if (task.getResult().getValue() != null) {
                            HashMap<String, Object> PokerTableGames = (HashMap<String, Object>) resultPokerTableGames.get("pokerTables");
                            HashMap<String, Object> resultPokerUsersToTables = (HashMap<String, Object>) resultPokerTableGames.get("usersToTables");
                            if(resultPokerUsersToTables.get("" + user.getEmail().replace(".", "*"))!=null) {//נוציא שחקן רק אם קיים בשולחן
                                int numTable = Integer.parseInt("" + resultPokerUsersToTables.get("" + user.getEmail().replace(".", "*")));
                                ArrayList<String> usersList = (ArrayList<String>) PokerTableGames.get("" + numTable);
                                int numOfUser = usersList.indexOf(user.getEmail());
                                if (numOfUser == 0) {//אם מי שמוחקים אותו הוא המנהל של השולחן
                                    for (int i = 0; i < usersList.size(); i++) {
                                        if (usersList.get(i).equals(user.getEmail())) {
                                            resultPokerUsersToTables.remove("" + usersList.get(i).replace(".", "*"));
                                        }
                                    }
                                    dataBaseRef.child("pokerTableGames").child("usersToTables").setValue(resultPokerUsersToTables);
                                    PokerTableGames.remove("" + numTable);
                                    dataBaseRef.child("pokerTableGames").child("pokerTables").setValue(PokerTableGames);//אחרי ששינינו את השולחנון והוספנו שולחן ברקע נשייך את המשתמש
                                } else {//לא המנהל אז מוציאים רק אותו
                                    resultPokerUsersToTables.remove("" + user.getEmail().replace(".", "*"));
                                    dataBaseRef.child("pokerTableGames").child("usersToTables").setValue(resultPokerUsersToTables);
                                    usersList.remove(user.getEmail());
                                    dataBaseRef.child("pokerTableGames").child("pokerTables").child("" + numTable).setValue(usersList);

                                }
                            }
                            //מה שמעל התעסק בלסדר את הטבלאות כך שיוציא את מי שמתחבר
                            //מה שמתחת שם אותו בשולחן עם החדש
                            ArrayList<String> usersListInTableNew = (ArrayList<String>) PokerTableGames.get("" + tablenum);
                            usersListInTableNew.add(user.getEmail());
                            dataBaseRef.child("pokerTableGames").child("pokerTables").child("" + tablenum).setValue(usersListInTableNew);
                            String appropriateEmail = user.getEmail().replace(".", "*");
                            dataBaseRef.child("pokerTableGames").child("usersToTables").child(appropriateEmail).setValue(tablenum);

                        }
                    }
                    catch (Exception e){
                        Toast.makeText(pokerGameFindTable.this,"eror",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }//פה עושה אותו דבר כמו הדליט הרגיל מוחק ובודק אם מנהל או לא אותו דבר קיצר רק שבסוף מוסיף איזה משהו לFIND
    public void returnToFindPage(){
        tvTableFindStatus.setText("");
        etWriteTableCode.setText("");
        tvTableOwner.setText("");
        tvTableNumber.setText("");
        btnOpenTable.setVisibility(View.VISIBLE);
        cardViewCreateTable.setVisibility(View.GONE);
        cardViewFindTable.setVisibility(View.VISIBLE);
        btnCreateTable.setAlpha((float)1);
        btnFindTable.setAlpha((float)0.5);
        btnCreateTable.setClickable(true);
        btnFindTable.setClickable(false);
        btnOpenTable.setClickable(true);
        FindTableTableProperties.setVisibility(View.GONE);
        deleteTable();
    }

    public void createWaitingScreen(int tableNum,String owner){
        waitingDialog= new Dialog(pokerGameFindTable.this);
        waitingDialog.setContentView(R.layout.waitingdialog);
        waitingDialog.setCancelable(false);
        btnCancelWaitingDialog = waitingDialog.findViewById(R.id.btnCancelWaiting);
        tvTableDetailsDialog = waitingDialog.findViewById(R.id.tvTableDetails);
        tvTableDetailsNumberDialog = waitingDialog.findViewById(R.id.tvTableDetailsNumber);
        btnCancelWaitingDialog.setOnClickListener(this);
        tvTableDetailsDialog.setText("Table owner: "+owner);
        tvTableDetailsNumberDialog.setText("Table number: "+tableNum);
        waitingDialog.show();
        waitingDialog.create();
        listenerToStartGame(tableNum);
    }
    public void listenerToStartGame(int numOfGame){
        dataBaseRef.child("pokerTableGames").child("activeGames").child(""+numOfGame).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    if (check==0) {
                        startGame = new Intent(pokerGameFindTable.this, pokerGameActivity.class);
                        startGame.putExtra("tableNumber", numOfGame);
                        startActivity(startGame);
                        check++;
                    }
                }
                catch (Exception e){

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void listenerToJoinPeople(int numOfTable){
        dataBaseRef.child("pokerTableGames").child("pokerTables").child(""+numOfTable).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> emailsArr = (ArrayList<String>) dataSnapshot.getValue();
                if (emailsArr != null && emailsArr.size() > 1) {
                    usersArrInMyTable = new ArrayList<User>();
                    if (documentUsersInFireBase != null) {
                        for (int i = 1; i < emailsArr.size(); i++) {
                            Map<String, Object> UserHash = (Map) documentUsersInFireBase.get(emailsArr.get(i));
                            User user = new User("" + UserHash.get("userName"), emailsArr.get(i));
                            user.setProfileImageUri(Uri.parse("" + UserHash.get("profileImageUri")));
                            usersArrInMyTable.add(user);
                        }
                        JoinTableAdapter = new JoinTableAdapter(pokerGameFindTable.this, 0, 0, usersArrInMyTable);
                        lvJoinTable.setVisibility(View.VISIBLE);
                        lvJoinTable.setAdapter(JoinTableAdapter);
                    } else {
                        for (int i = 1; i < emailsArr.size(); i++) {
                            User user = new User("", emailsArr.get(i));
                            usersArrInMyTable.add(user);
                        }
                        JoinTableAdapter = new JoinTableAdapter(pokerGameFindTable.this, 0, 0, usersArrInMyTable);
                        lvJoinTable.setVisibility(View.VISIBLE);
                        lvJoinTable.setAdapter(JoinTableAdapter);

                    }
                }
                else{
                    lvJoinTable.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    public void downloadFireStoreInBackground(){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docRef1 = db.collection("Users").document("usersDetail");
        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {//פותח את המסמך של המשתמשים
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        documentUsersInFireBase = document.getData();
                    }
                }
            }
        });
    }
    public void setSeekbars(SeekBar seekbar1, TextView tv1, SeekBar seekbar2, TextView tv2){
        seekbar1.setMax(100);
        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tv1.setText(progress*10+"$");
                //  Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekbar2.setMax(10);
        seekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tv2.setText(progress*10+"$");
                //  Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        if (startGame==null) {
             /*          timerToGetBack=new CountDownTimer(7000,1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    deleteTable();
                    onClick(btnCreateTable);
                }
            };*/
            deleteTable();
            onClick(btnCreateTable);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        //   if (timerToGetBack!=null){
        //        timerToGetBack.cancel();
        //      timerToGetBack=null;
        //    Toast.makeText(this,"cksc",Toast.LENGTH_SHORT).show();
        //   }
    }

}