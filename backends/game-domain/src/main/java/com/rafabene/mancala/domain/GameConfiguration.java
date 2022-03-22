package com.rafabene.mancala.domain;

public class GameConfiguration {

    private int numberOfPits = 6;
    private int numberOfStones = 6;

    public GameConfiguration(){
        
    }

    public GameConfiguration(int numberOfPits, int numberOfStones) {
        this.numberOfPits = numberOfPits;
        this.numberOfStones = numberOfStones;
    }

    public int getNumberOfPits() {
        return this.numberOfPits;
    }

    public int getNumberOfStones() {
        return this.numberOfStones;
    }

}
