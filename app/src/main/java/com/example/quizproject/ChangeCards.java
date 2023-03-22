package com.example.quizproject;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeCards extends Thread {

    DatabaseReference dataBaseRef;
    int tableNum;
    StorageReference storageRef;
    Context context;
    public ChangeCards() {

    }

    @Override
    public void run() {

    }
    /*
    public void addCards(){
        dataBaseRef.child("pokerTableGames").child("activeGames").child("" + tableNum).child("Cards").child("CardsBoard").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ArrayList<HashMap> board=(ArrayList<HashMap> )task.getResult().getValue();
                displayCardBoard(board,3,0,true);
            }
        });
    }

    public void displayCardBoard(ArrayList<HashMap> board,int numOfRound,int indexToRecurtion,boolean ifNeenAllCards ){
        if (ifNeenAllCards){
            String suit = "" + board.get(indexToRecurtion).get("suit");
            storageRef.child("profilePicturs/pokerCards/" + suit + helpToDisplayCardBoard(Integer.parseInt("" + board.get(indexToRecurtion).get("numOfValue"))) + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (indexToRecurtion == 0) {
                        Glide.with(pokerGameActivity.class).load(uri).into(cardFlopNumber1);
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

     */
}
