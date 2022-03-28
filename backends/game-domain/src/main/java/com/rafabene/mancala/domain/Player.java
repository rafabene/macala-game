package com.rafabene.mancala.domain;

public class Player {
    
    private int gamesWon;

    private int gamesLoose;

    private String gamerId;

    public Player(String gamerId) {
        this.gamerId = gamerId;
    }

    public String getGamerId() {
        return gamerId;
    }

    public int getGamesPlayed() {
        return gamesWon + gamesLoose;
    }

    public int getGamesLoose() {
        return gamesLoose;
    }

    public void wonGame() {
        gamesWon++;
    }

    public void gameLost() {
        gamesLoose++;
    }

    public int getGamesWon() {
        return gamesWon;
    }

}
