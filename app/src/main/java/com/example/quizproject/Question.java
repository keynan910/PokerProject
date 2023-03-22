package com.example.quizproject;

import java.util.Random;

public class Question {
    private  String question;
    private String[] answersArr;
    private int numOfCorrectAnswer;
    private boolean isUsed;

    public Question(String question, String[] answersArr, int numOfCorrectAnswer,boolean isUsed) {
        this.question = question;
        this.answersArr = answersArr;
        this.numOfCorrectAnswer = numOfCorrectAnswer;
        this.isUsed=isUsed;
    }
    public boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    public String[] getAnswersArr() {
        return answersArr;
    }

    public void setAnswersArr(String[] answersArr) {
        this.answersArr = answersArr;
    }

    public int getNumOfCorrectAnswer() {
        return numOfCorrectAnswer;
    }

    public void setNumOfCorrectAnswer(int numOfCorrectAnswer) {
        this.numOfCorrectAnswer = numOfCorrectAnswer;
    }
    public void shuffle(){
        String correctAnswer = answersArr[numOfCorrectAnswer-1];
        String[] answersArrCopy=new String[4];
        for (int i = 0; i <answersArr.length; i++) {
            answersArrCopy[i]="####";
        }
        for (int i = 0; i <answersArr.length; i++) {
            int num= (int)(Math.random()*4);
            if (answersArrCopy[num].equals("####")){
                answersArrCopy[num]=answersArr[i];
            }
            else{
                if (i==3){
                    for (int j = 0; j < 4; j++) {
                        if (answersArrCopy[j].equals("####")){
                            answersArrCopy[j]=answersArr[i];
                        }
                    }
                    break;
                }
                i--;
            }
        }
        for (int i = 0; i <answersArr.length; i++) {
            answersArr[i]=answersArrCopy[i];
            if (answersArr[i].equals(correctAnswer)){
                numOfCorrectAnswer=i;
            }
        }
    }




}
