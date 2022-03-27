package com.rafabene.mancala.domain;

public class Pit{

    private int numberOfStones;

    public Pit() {
    }

    public Pit(int numberOfStones) {
        this.numberOfStones = numberOfStones;
    }

    public int getNumberOfStones() {
        return numberOfStones;
    }

    public void setNumberOfStones(int numberOfStones) {
        this.numberOfStones = numberOfStones;
    }

    @Override
    public String toString() {
        return "{" +
            " numberOfStones='" + getNumberOfStones() + "'" +
            "}";
    }

    
}