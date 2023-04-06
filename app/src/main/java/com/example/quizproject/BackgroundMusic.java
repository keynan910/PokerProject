package com.example.quizproject;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundMusic extends Service {
    private MediaPlayer backgroundMusicPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.dumb_ways_to_die_background_music);
        backgroundMusicPlayer.setLooping(true);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}