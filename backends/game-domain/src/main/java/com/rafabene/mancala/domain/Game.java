package com.rafabene.mancala.domain;

import javax.json.bind.annotation.JsonbTransient;

public class Game {

    private Player[] players = new Player[2];

    private GameStatus gameStatus = GameStatus.NOT_RUNNING;

    private Board board;

    private Player winner;

    private Player playerTurn;

    private GameConfiguration gameConfiguration;

    public Game() {
        this(new GameConfiguration());
    }

    public Game(GameConfiguration configuration) {
        this.gameConfiguration = configuration;
        board = new Board(
                gameConfiguration.getNumberOfPits(),
                gameConfiguration.getNumberOfStones());
    }

    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    public void startGame() throws IllegalGameStateException {
        if (gameStatus.equals(GameStatus.RUNNING)) {
            throw new IllegalGameStateException("Game is already running!");
        }
        if (players[0] != null && players[1] != null) {
            getBoard().reset();
            playerTurn = players[0];
            winner = null;
            gameStatus = GameStatus.RUNNING;
        } else {
            throw new IllegalGameStateException("We can't start the game without 2 players");
        }
    }

    public void stopGame() {
        gameStatus = GameStatus.NOT_RUNNING;
        playerTurn = null;
    }

    /**
     * Seed the stones in the pit informed in the parameter.
     * 
     * The player turn will be updated according to Mancala's rule.
     * 
     * @param pit position of the pit: 1 to pitsQuantity
     * @throws IllegalGameMoveException  if the pit informed is greatter than
     *                                   pitsQuantiy.
     * @throws IllegalGameStateException if the game is not running
     * 
     * @see GameStatus
     */
    public void move(int pit) throws IllegalGameMoveException, IllegalGameStateException {
        if (!gameStatus.equals(GameStatus.RUNNING)) {
            throw new IllegalGameStateException("Can't move if the game hasn't began. ");
        }
        boolean playAgain = new WalkableBoard(
                this.getBoard(), 
                this.getPlayers(), 
                this.getPlayerTurn())
            .move(pit);

        // Verify Game over before switching players
        verifyGameOver();

        // If player shouldn't play again
        if (!playAgain) {
            switchPlayerTurn();
        }
    }

    private void verifyGameOver() {
        int sumPlayer1 = 0;
        int sumPlayer2 = 0;
        for (int x = 0; x < getBoard().getPlayer1Pits().length; x++) {
            sumPlayer1 += board.getPlayer1Pits()[x].getNumberOfStones();
            sumPlayer2 += board.getPlayer2Pits()[x].getNumberOfStones();
        }
        if (sumPlayer1 == 0 || sumPlayer2 == 0) {
            gameStatus = GameStatus.GAME_OVER;
            winner = getPlayerTurn();
            winner.wonGame();

            // Mark looser
            if (players[0].equals(winner)) {
                players[1].gameLost();
            } else {
                players[0].gameLost();
            }
        }
    }

    private void switchPlayerTurn() {
        if (playerTurn.equals(players[0])) {
            playerTurn = players[1];
        } else {
            playerTurn = players[0];
        }
    }

    public void addNewPlayer(Player newPlayer) throws IllegalGameStateException {
        if (players[0] != null && players[1] != null) {
            throw new IllegalGameStateException("There are two players at this moment. Retry later.");
        } else if (players[0] == null) {
            players[0] = newPlayer;

        } else if (players[1] == null) {
            players[1] = newPlayer;
        }
    }

    /**
     * 
     * @param player Player to be removed
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

    @Override
    public String toString() {
        return "{" +
                ", players='" + getPlayers() + "'" +
                ", board='" + getBoard() + "'" +
                ", gameStatus='" + getGameStatus() + "'" +
                ", playerTurn='" + getPlayerTurn() + "'" +
                ", winner='" + getWinner() + "'" +
                "}";
    }

    @JsonbTransient // Web doesn't need the internal board. We can make it transient in the Json
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

    public Player getWinner() {
        return winner;
    }


}
