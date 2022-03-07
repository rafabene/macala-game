package com.rafabene.macala.domain;

public class Game {

    private Player[] players = new Player[2];

    private Board board = new Board();

    private GameStatus gameStatus = GameStatus.NOT_RUNNING;

    private static Game instance;

    private Player playerTurn;

    private Game() {

    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void startGame() throws IllegalGameStageException {
        if (players[0] != null && players[1] != null) {
            board.reset();
            playerTurn = players[0];
            gameStatus = GameStatus.RUNNING;
        } else {
            throw new IllegalGameStageException("We can't start the game without 2 players");
        }
    }

    @Override
    public String toString() {
        return "{" +
                " players='" + players + "'" +
                ", board='" + board + "'" +
                "}";
    }

    public Board getBoard() {
        return board;
    }

    public Player[] getPlayers() {
        return players;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Player getPlayerTurn() {
        return playerTurn;
    }

    /**
     * 
     * @param player
     * @return true if player was playing. False otherwise
     */
    public boolean removePlayer(Player player) {
        gameStatus = GameStatus.NOT_RUNNING;
        if (player.equals(players[0])) {
            players[0] = null;
            return true;
        }
        if (player.equals(players[1])) {
            players[1] = null;
            return true;
        }
        return false;
    }

    public void addNewPlayer(Player newPlayer) throws IllegalGameStageException {
        if (players[0] != null && players[1] != null) {
            throw new IllegalGameStageException("There are two players at this moment. Retry later.");
        } else if (players[0] == null) {
            players[0] = newPlayer;

        } else if (players[1] == null) {
            players[1] = newPlayer;
        }
    }

}
