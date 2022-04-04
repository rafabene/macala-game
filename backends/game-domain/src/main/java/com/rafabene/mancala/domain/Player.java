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

    @Override
    public String toString() {
        return "Player [gamerId=" + gamerId + ", gamesLoose=" + gamesLoose + ", gamesWon=" + gamesWon + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gamerId == null) ? 0 : gamerId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (gamerId == null) {
            if (other.gamerId != null)
                return false;
        } else if (!gamerId.equals(other.gamerId))
            return false;
        return true;
    }

    

}
