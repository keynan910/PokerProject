package com.example.quizproject;

import android.os.Handler;
import android.os.Looper;

public class GameClock implements Runnable{
    private ClockListener listener;
    private boolean running = true;
    private final int START_TIME;
    private int millis = 0;
    private int seconds = 0;
    private int minutes = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());



    public GameClock(ClockListener listener, int totalSeconds) {
        this.listener = listener;

        minutes = totalSeconds / 60;
        seconds = totalSeconds - (60 * minutes);

        START_TIME = totalSeconds;
    }


    public void update(int updateMillis) {
        if (running) {
            millis -= updateMillis;
            if (millis<1 && seconds>=1){
                seconds--;
                millis+=1000;
                listener.onSecondTickClock(seconds);
            }
            else if (millis<1 && seconds<1){
                finish();
            }
        }
    }

    public void reset() {
        millis = 0;
        minutes = START_TIME / 60;
        seconds = START_TIME - (60 * minutes);
    }
    public void finish() {
        millis = 0;
        minutes = 0;
        seconds = 0;
        running=false;
        listener.onFinishClock();
    }

    public String getCurrentTime() {
        String strMinutes = minutes + "", strSeconds = seconds + "", strMillis = millis + "";
        if (minutes < 10) strMinutes = "0" + strMinutes;
        if (seconds < 10) strSeconds = "0" + strSeconds;
        if (millis < 10) strMillis = "0" + strMillis;

        return strMinutes + ":" + strSeconds + ":" + strMillis;
    }

    public void stop(){
        running=false;
    }
    public void resume(){
        running=true;
        seconds=30;
        millis=0;
        minutes=0;
    }

    @Override
    public void run() {
        if (running) {
            update(1000);
        }
        handler.postDelayed(this, 1000);
    }

    public interface ClockListener {
        void onSecondTickClock(int seconds);
        void onFinishClock();
    }
//jdvjhsvjsbv new
}