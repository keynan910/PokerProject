package com.example.quizproject;
import android.net.Uri;

public class User {
    private String userName;
    private String email;
    private int points;
    private Uri profileImageUri;

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
        points=-1;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Uri getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(Uri profileImageUri) {
        this.profileImageUri = profileImageUri;
    }
}
