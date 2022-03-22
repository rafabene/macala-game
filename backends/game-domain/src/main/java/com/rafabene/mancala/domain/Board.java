package com.rafabene.mancala.domain;

public class Board {

    private int[] player1Pits;
    private int[] player2Pits;
    private Mancala player1Mancala;
    private Mancala player2Mancala;

    private int defaultNumberofStones;

    public Board(){
        this(6,6);
    }

    public Board(int numberOfPits, int numberOfStones) {
        player1Mancala = new Mancala();
        player2Mancala = new Mancala();
        player1Pits = new int[numberOfPits];
        player2Pits = new int[numberOfPits];
        this.defaultNumberofStones = numberOfStones;
        reset();
    }

    public void reset() {
        for(int x=0; x < player1Pits.length; x++){
            player1Pits[x] = defaultNumberofStones;
            player2Pits[x] = defaultNumberofStones;
        }
    }

    public int[] getPlayer1Pits() {
        return player1Pits;
    }

    public int[] getPlayer2Pits() {
        return player2Pits;
    }

    public Mancala getPlayer1Mancala() {
        return this.player1Mancala;
    }
    

    public Mancala getPlayer2Mancala() {
        return this.player2Mancala;
    }

    @Override
    public String toString() {
        return "{" +
                " player1Pits='" + getPlayer1Pits() + "'" +
                ", player2Pits='" + getPlayer2Pits() + "'" +
                ", player1Macala='" + getPlayer1Mancala() + "'" +
                ", player2Macala='" + getPlayer2Mancala() + "'" +
                "}";
    }


}
