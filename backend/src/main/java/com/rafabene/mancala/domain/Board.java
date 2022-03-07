package com.rafabene.mancala.domain;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class Board {

    private int[] player1Pits;
    private int[] player2Pits;
    private int player1Mancala;
    private int player2Mancala;

    public Board() {
        reset();
    }

    public void reset() {
        Config config = ConfigProvider.getConfig();
        int stonesQuantity = config.getValue("stonesQuantity", Integer.class);
        int pitsQuantity = config.getValue("pitsQuantity", Integer.class);
        player1Mancala = 0;
        player2Mancala = 0;
        player1Pits = new int[pitsQuantity];
        player2Pits = new int[pitsQuantity];
        for(int x=0; x < pitsQuantity; x++){
            player1Pits[x] = stonesQuantity;
            player2Pits[x] = stonesQuantity;
        }
    }

    public int[] getPlayer1Pits() {
        return player1Pits;
    }

    public int[] getPlayer2Pits() {
        return player2Pits;
    }

    public void setPlayer1Mancala(int player1Mancala) {
        this.player1Mancala = player1Mancala;
    }

    public int getPlayer1Mancala() {
        return this.player1Mancala;
    }
    
    public void setPlayer2Mancala(int player2Mancala) {
        this.player2Mancala = player2Mancala;
    }

    public int getPlayer2Mancala() {
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
