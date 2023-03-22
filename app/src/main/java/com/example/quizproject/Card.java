package com.example.quizproject;

public class Card  implements Comparable{
    private int numOfValue;//2-14
    private String Suit;

    public Card(int numOfValue, String suit) {
        this.numOfValue = numOfValue;
        Suit = suit;
    }

    public int getNumOfValue() {
        return numOfValue;
    }

    public void setNumOfValue(int numOfValue) {
        this.numOfValue = numOfValue;
    }

    public String getSuit() {
        return Suit;
    }

    public void setSuit(String suit) {
        Suit = suit;
    }



    //משמש לSORT של ARRAYLIST
    @Override
    public int compareTo(Object compareCard) {
        int compareNum=((Card)compareCard).getNumOfValue();
        return this.numOfValue-compareNum;
    }


}
