package com.example.quizproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class winnersActivity extends AppCompatActivity {

    ArrayList<User> usersList = new ArrayList<User>();
    Map<String, Object> documentUserHash, documentLeaderBoardHash;
    private FirebaseAuth mAuth;
    UsersAdapter usersAdapter;
    ListView lvCustomLayoutUsers;
    Intent it;
    int sumPointsUser;
    FirebaseFirestore db;
    Dialog loadingpage;
    TextView userFirstPlace, userSecondPlace, userThirdPlace, pointsFirstPlace, pointsSecondPlace, pointsThirdPlace;
    ConstraintLayout constraintSecondPlace, constraintThirdPlace;
    ImageView profileImageFirst, profileImageSecond, profileImageThird;
    SharedPreferences.Editor edit;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winners);
        userFirstPlace = findViewById(R.id.userFirstPlace);
        userSecondPlace = findViewById(R.id.userSecondPlace);
        userThirdPlace = findViewById(R.id.userThirdPlace);
        pointsFirstPlace = findViewById(R.id.pointsFirstPlace);
        pointsSecondPlace = findViewById(R.id.pointsSecondPlace);
        pointsThirdPlace = findViewById(R.id.pointsThirdPlace);
        constraintSecondPlace = findViewById(R.id.constraintSecondPlace);
        constraintThirdPlace = findViewById(R.id.constraintThirdPlace);
        profileImageFirst = findViewById(R.id.profileImageFirst);
        profileImageSecond = findViewById(R.id.profileImageSecond);
        profileImageThird = findViewById(R.id.profileImageThird);
        sp = getSharedPreferences("info", 0);
        edit = sp.edit();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadingpage = new Dialog(this);
        loadingpage.setContentView(R.layout.loading_layout);
        loadingpage.setCancelable(false);
        loadingpage.show();
        it = getIntent();
        if (it.getIntExtra("points", -1) > -1) {
            sumPointsUser = it.getIntExtra("points", -1);
            //  אם מתאים נוסיף את המשתמש לleaderboard
            DocumentReference docRef1 = db.collection("Users").document("leaderboard");
            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {//TODO לבדוק על הקוד הזה פה הוא לא טוב לשנות חוץ מזה
                //תזכורת ב שרד יש לנו את הרשימה של אימיילים נקודות ושל תמונות בתור ביטמפ אבל רק של המשתמשים מהמכשיר הזה כלומר מיועד רק לבדוק את מצב המשתמש
                // TODO לבדוק אם אני מתחבר ב LOGIN
                //TODO שזה משתמש שהתחברתי ממנו פה פעם ולא סתם משתמש שהיה רשום בטלפון אחר כי אז צריך לתקן את השרד כי לא יעבוד הפרופיל של השחקן לחשוב מה לעשות אולי לקחת מטבלת המשתמשים
                //נוסף דף ההישגים חייב לקבל באינטנט מספר נקודות כדי לדעת אם שיפר או לא ואם אני עובר אליו לא מעמוד האוכל אז הוא יקבל כדיפולט -1 ולעשות שאם יש -1 אז רק מציג את הטבלה כמו שהיא בלי שינויים
                // בנוסף יש תמונה בתוך הuserURL
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {//פותח את המסמך של הטבלת הישגים
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            documentLeaderBoardHash = document.getData();
                            if (documentLeaderBoardHash.size() != 0) {
                                //מעדכן את הטבלה להיות מסודרת את המסמך
                                Map<String, Object> temp = new HashMap<>();//אי אשפר לסדר מילון אז נכניס פשווט את העשר הכי טובים ואז נסדר במערך
                                int numOfArr = 0;
                                if (documentLeaderBoardHash.size() < 10) {//TODO EXPLAIN בגלל שלמילון לכל KEY רק VALUE אחד ואנחנו יודעים שבוודאות או שיפר או פעם ראשונה אז אפשר פשוט ישר להוסיף אותו לDOCUMENT אם קטן מ 10
                                    documentLeaderBoardHash.put(mAuth.getCurrentUser().getEmail(), sumPointsUser);
                                }
                                else {
                                    boolean tryToDoBetter=false;
                                    numOfArr = 10;
                                    String[] emails = new String[numOfArr];
                                    Integer[] points = new Integer[numOfArr];
                                    Integer [] pointsTemp = new Integer[numOfArr];
                                    int i = 0;
                                    for (Map.Entry<String, Object> entry1 : documentLeaderBoardHash.entrySet()) {
                                        emails[i] = entry1.getKey();
                                        points[i] = Integer.parseInt("" + entry1.getValue());
                                        pointsTemp[i]=Integer.parseInt("" + entry1.getValue());
                                        i++;
                                        if (entry1.getKey().equals(mAuth.getCurrentUser().getEmail())){// אם בוודאות יודעים שמישהו כבר היה פה ומנסה לשפר אז בגלל שלכל KEY יש ערך אחד אז פשוט נחליף
                                            documentLeaderBoardHash.put(mAuth.getCurrentUser().getEmail(), sumPointsUser);
                                            tryToDoBetter=true;
                                        }
                                    }
                                    if (!tryToDoBetter) {// אם זה מישהו שלא היה בטבלת הישגים ומנסה להיכנס
                                        Arrays.sort(points, Collections.reverseOrder());
                                        if (points[documentLeaderBoardHash.size() - 1] < sumPointsUser) {//נבדוק אם הוא בגלל גדול מהאיש האחרון
                                            for (Map.Entry<String, Object> entry1 : documentLeaderBoardHash.entrySet()) {//אם כן נוציא את האחרון פשוט
                                                int num = Arrays.asList(pointsTemp).indexOf(points[documentLeaderBoardHash.size() - 1]);
                                                if (entry1.getKey().equals(emails[num])){
                                                    temp.put(mAuth.getCurrentUser().getEmail(), sumPointsUser);
                                                }
                                                else{
                                                    temp.put(entry1.getKey(), entry1.getValue());
                                                }
                                            }
                                            documentLeaderBoardHash = temp;
                                        }
                                    }
                                }
                            }
                            //לאחר ששינינו את טבלת ההישגים אז נעדכן ברקע את הטבלת הישגים
                            changeLeaderboard();//בזמן שהטבלה משתנה נפתח את מסמך הUSERS בכדי להוציא את התמונה והשם של כל משתמש שנמצא בטופ של הLEADERBOARD
                            DocumentReference docRef1 = db.collection("Users").document("usersDetail");
                            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {//פותח את המסמך של הטבלת הישגים
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            documentUserHash = document.getData();
                                            getListOfUsers();//בזמן שהטבלה משתנת נעדכן את רשימת המשתמשים
                                            addAllUsers();//לאחר שיש את רשימת משתמשים נוסיף את כולם לVIEW ונציג בזמן שהטבלה מתעדכנת ברקע
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            //אם לא שיפר תוצאה טבלת המנצחים נשארת אותו דבר וניקח אותה מכאן
            DocumentReference docRef1 = db.collection("Users").document("leaderboard");
            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {//פותח את המסמך של הטבלת הישגים
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            documentLeaderBoardHash = document.getData();
                            //בזמן שהטבלה משתנה נפתח את מסמך הUSERS בכדי להוציא את התמונה והשם של כל משתמש שנמצא בטופ של הLEADERBOARD
                            DocumentReference docRef1 = db.collection("Users").document("usersDetail");
                            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {//פותח את המסמך של הטבלת הישגים
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            documentUserHash = document.getData();
                                            getListOfUsers();// בזמן שהטבלה משתנת נעדכן את רשימת המשתמשים ובגלל שאין סדר בMAP צריך לסדר מחדש ולהביא רשימה מסודרת
                                            addAllUsers();//לאחר שיש את רשימת משתמשים נוסיף את כולם לVIEW ונציג בזמן שהטבלה מתעדכנת ברקע
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }

    }

    public void changeLeaderboard() {
        db.collection("Users").document("leaderboard")
                .set(documentLeaderBoardHash)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(winnersActivity.this, "the leaderboard Changed", Toast.LENGTH_SHORT).show();
                        setNotification();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(winnersActivity.this, "the leaderboard Changed Fail", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void getListOfUsers() {//documentLeaderBoardHash נעזר ב ומעדכן את רשימת המשתמשים לרשימה עם כל המשתמשים לפי סדרן
        String[] emails = new String[documentLeaderBoardHash.size()];
        Integer[] points = new Integer[documentLeaderBoardHash.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry1 : documentLeaderBoardHash.entrySet()) {
            emails[i] = entry1.getKey();
            points[i] = Integer.parseInt("" + entry1.getValue());
            i++;
        }
        Arrays.sort(points, Collections.reverseOrder());

        Boolean[] placesInArr = new Boolean[documentLeaderBoardHash.size()];
        for (int j = 0; j < placesInArr.length; j++) {
            placesInArr[j] = false;
        }

        for (Map.Entry<String, Object> entry1 : documentLeaderBoardHash.entrySet()) {
            int num = Arrays.asList(points).indexOf(Integer.parseInt("" + entry1.getValue()));
            if (!placesInArr[num]) {
                emails[num] = entry1.getKey();
                placesInArr[num] = true;
            } else {
                for (int j = num; j < documentLeaderBoardHash.size(); j++) {
                    if (placesInArr[j] == false) {
                        emails[j] = entry1.getKey();
                        placesInArr[j] = true;
                        break;
                    }
                }
            }
        }
        for (int j = 0; j < documentLeaderBoardHash.size(); j++) {
            int numPoints = Integer.parseInt("" + points[j]);
            Map<String, Object> UserHash = (Map) documentUserHash.get(emails[j]);
            String email = "" + UserHash.get("email");
            String userName = "" + UserHash.get("userName");
            String profilePhotoUrl = "" + UserHash.get("profileImageUri");
            User user = new User(userName, email);
            user.setPoints(numPoints);
            user.setProfileImageUri(Uri.parse(profilePhotoUrl));
            usersList.add(user);
            //מעדכן את הרשימת users בusersList
        }
    }

    public void addAllUsers() {
        ArrayList<User> usersListTemp = new ArrayList<User>(); //נתחיל בלהוסיף את השלושה הראשונים
        //מוסיף למקומות ידנית כל USER
        if (usersList.size() == 0) {
            constraintSecondPlace.setVisibility(View.GONE);
            constraintThirdPlace.setVisibility(View.GONE);
            userFirstPlace.setText("no one yet try to get the crown");
        }
        if (usersList.size() == 1) {
            constraintSecondPlace.setVisibility(View.GONE);
            constraintThirdPlace.setVisibility(View.GONE);
            User user = usersList.get(0);
            userFirstPlace.setText("" + user.getUserName());
            pointsFirstPlace.setText("" + user.getPoints());
            Glide.with(winnersActivity.this).load(user.getProfileImageUri()).into(profileImageFirst);
        } else if (usersList.size() == 2) {
            constraintThirdPlace.setVisibility(View.GONE);
            constraintSecondPlace.setVisibility(View.VISIBLE);
            User user = usersList.get(0);
            userFirstPlace.setText(user.getUserName());
            pointsFirstPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageFirst);
            user = usersList.get(1);
            userSecondPlace.setText(user.getUserName());
            pointsSecondPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageSecond);
        } else if (usersList.size() == 3) {
            constraintThirdPlace.setVisibility(View.VISIBLE);
            constraintSecondPlace.setVisibility(View.VISIBLE);
            User user = usersList.get(0);
            userFirstPlace.setText(user.getUserName());
            pointsFirstPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageFirst);
            user = usersList.get(1);
            userSecondPlace.setText(user.getUserName());
            pointsSecondPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageSecond);
            user = usersList.get(2);
            userThirdPlace.setText(user.getUserName());
            pointsThirdPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageThird);
        } else {//אם יש יותר מ3 מנצחים אז את השלושה הראשונים שמים ידנית והשאר מורידים אותם מהרשימה של הUSER ועושים עם LISTVIEW
            constraintThirdPlace.setVisibility(View.VISIBLE);
            constraintSecondPlace.setVisibility(View.VISIBLE);
            User user = usersList.get(0);
            userFirstPlace.setText(user.getUserName());
            pointsFirstPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageFirst);
            user = usersList.get(1);
            userSecondPlace.setText(user.getUserName());
            pointsSecondPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageSecond);
            user = usersList.get(2);
            userThirdPlace.setText(user.getUserName());
            pointsThirdPlace.setText("" + user.getPoints());
            Glide.with(this).load(user.getProfileImageUri()).into(profileImageThird);
            for (int i = 3; i < usersList.size(); i++) {
                usersListTemp.add(usersList.get(i));
            }
            usersAdapter = new UsersAdapter(winnersActivity.this, 0, 0, usersListTemp);
            lvCustomLayoutUsers = findViewById(R.id.lvCustomLayoutUsers);
            lvCustomLayoutUsers.setAdapter(usersAdapter);
        }
        loadingpage.cancel();//אחרי שהכל במקום זה הזמן לסגור את הLOADING




        //שולח לפעולה נוספת שתבדוק את מי עקפתי ותשלח לו נוטיפיקציה לטלפון
    }

    public void setNotification(){

        int numPoints=-1;
        int numMyPlace=-1;
        ArrayList<Map.Entry<String,Object>> arrayList=new ArrayList<Map.Entry<String,Object>>();
        for (Map.Entry<String,Object> entry:documentLeaderBoardHash.entrySet()){
            if (entry.getKey().equals(mAuth.getCurrentUser().getEmail())){
                if (Integer.parseInt(""+entry.getValue())==it.getIntExtra("points", -1) && numPoints==-1){
                    numPoints=Integer.parseInt(""+entry.getValue());
                }
            }
            arrayList.add(entry);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            arrayList.sort((x,y) -> Integer.parseInt(""+x.getValue())-Integer.parseInt(""+y.getValue()));
        }
        ArrayList<String> arrayList2=new ArrayList<String>();

        for (int i = 0; i < arrayList.size(); i++) {
            if (it.getIntExtra("points", -1) == Integer.parseInt(""+arrayList.get(i).getValue())){
                break;
            }
            arrayList2.add(arrayList.get(i).getKey());

        }
        if (arrayList2.size()>0) {
            setNotificationHelp(arrayList2);
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
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
            intent.putExtra("points", -1);
            startActivity(intent);
        } else if (id == R.id.mainPage) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.disconnect) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            mAuth.signOut();
            Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.profileStatus) {
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
                    Dialog loadingpage = new Dialog(winnersActivity.this);
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
                                            Toast.makeText(winnersActivity.this, "" + mAuth.getCurrentUser().getEmail() + " have " + pointsArr[index] + " points", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(winnersActivity.this, "" + mAuth.getCurrentUser().getEmail() + "have no points yet", Toast.LENGTH_SHORT).show();
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
        return true;
    }

    public void setNotificationHelp(ArrayList<String> list){
        String messege ="nice good job you passed: "+list.size()+" people";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notification my", "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(winnersActivity.this, "notification my");
        notificationBuilder.setContentTitle("congratulations");
        notificationBuilder.setContentText(messege);
        notificationBuilder.setSmallIcon(R.drawable.raiseiconpoker);
        notificationBuilder.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(winnersActivity.this);
        managerCompat.notify(0, notificationBuilder.build());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notification my", "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
        }


    }

}