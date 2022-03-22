package com.rafabene.mancala.domain;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Player {" +
                "gamerId='" + getGamerId() + "'" +
                ", gamesWon='" + getGamesWon() + "'" +
                ", gamesLoose='" + getGamesLoose() + "'" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Player)) {
            return false;
        }
        Player player = (Player) o;
        return gamesWon == player.gamesWon && gamesLoose == player.gamesLoose
                && Objects.equals(gamerId, player.gamerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamesWon, gamesLoose, gamerId);
    }
}
