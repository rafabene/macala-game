package com.rafabene.mancala.domain;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class Game {

    private static Game instance;

    private Player[] players = new Player[2];

    private Board internalBoard = new Board();

    private GameStatus gameStatus = GameStatus.NOT_RUNNING;

    private Player playerTurn;

    /**
     * This is a singleton class. Please use the method
     * 
     * @see getInstance() to get a Game instance
     */
    private Game() {

    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void startGame() throws IllegalGameStateException {
        if (gameStatus.equals(GameStatus.RUNNING)) {
            throw new IllegalGameStateException("Game is already running!");
        }
        if (players[0] != null && players[1] != null) {
            getBoard().reset();
            playerTurn = players[0];
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
     * Reset the board, and return the player turn to player1.
     */
    public void reset() {
        playerTurn = players[0];
        getBoard().reset();
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
        WalkableBoard walkableBoard = new WalkableBoard();
        boolean playAgain = walkableBoard.move(pit);
        // If player shouldn't play again
        if (!playAgain) {
            changePlayerTurn();
        }
    }

    private void changePlayerTurn() {
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
                "}";
    }

    public Board getBoard() {
        return internalBoard;
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
     * An Walkable board contains an array in the following format:
     * 
     * [p1, p1, p1, p1, mancala1, p2, p2, p2, p2, mancala2]
     * 
     * It's formed by player1Pits + player1Mancala + player2Pits + player2Mancala
     * 
     * This instance should be used for each move
     * 
     */
    private class WalkableBoard {

        private int[] walkableBoard;

        private int getPitsQuantity() {
            Config config = ConfigProvider.getConfig();
            return config.getValue("pitsQuantity", Integer.class);
        }

        public WalkableBoard() {
            int pitsQuantity = getPitsQuantity();
            walkableBoard = new int[(pitsQuantity * 2) + 2];
            for (int x = 0; x < pitsQuantity; x++) {
                walkableBoard[x] = getPlayerTurnPits()[x];
                // +1 is needed to skip Player's Mancala on the last position
                walkableBoard[x + pitsQuantity + 1] = getOpponentPits()[x];
            }
            walkableBoard[pitsQuantity] = getPlayerTurnMancala().getContent();
            // Last positon
            walkableBoard[walkableBoard.length - 1] = getOpponentMancala().getContent();
        }

        /**
         * Seed the stones in the pit informed in the parameter.
         * 
         * @param pit position of the pit: 1 to pitsQuantity
         * @return true if the last positon is in current's player board
         * @throws IllegalGameMoveException if the pit informed is greatter than
         *                                  pitsQuantiy.
         */
        public boolean move(int pit) throws IllegalGameMoveException {
            int pitsQuantity = getPitsQuantity();
            int pitPosition = pit - 1;
            if (pit < 1 || pit > pitsQuantity) {
                throw new IllegalGameMoveException(String
                        .format("Can't move pit %s because each player has only %s pits", pitPosition, pitsQuantity));
            }
            // Number of stones in the pit
            int stones = walkableBoard[pitPosition];
            // Remove all stones in the pit
            walkableBoard[pitPosition] = 0;
            int walkableBoardPosition = 0;
            while (stones >= 0) {
                // Go to next pit
                pitPosition++;

                // Circular position
                walkableBoardPosition = (int) (pitPosition) % walkableBoard.length;

                // Skip opponnent's Mancala
                if (walkableBoardPosition == walkableBoard.length - 1) {
                    continue;
                }

                // Add a stone to the Pit
                walkableBoard[walkableBoardPosition]++;
                stones--;

            }
            fillInternalBoard();
            // Return true if we are in our side of the board after moving (pits + mancala)
            return walkableBoardPosition <= pitsQuantity + 1;
        }

        /**
         * Get the walkable board in the format of
         * [p1, p1, p1, p1, mancala1, p2, p2, p2, p2, mancala2]
         * 
         * and place the proper falues in the fields of the internal Board.
         */
        private void fillInternalBoard() {
            int pitsQuantity = getPitsQuantity();
            for (int x = 0; x < pitsQuantity; x++) {
                getPlayerTurnPits()[x] = walkableBoard[x];
                // +1 to skip Player's Mancala
                getOpponentPits()[x] = walkableBoard[x + pitsQuantity + 1];
            }
            // Player Mancala
            getPlayerTurnMancala().setContent(walkableBoard[pitsQuantity]);
            // Last position for Opponents Mancala
            getOpponentMancala().setContent(walkableBoard[(walkableBoard.length - 1)]);
        }

        private int[] getPlayerTurnPits() {
            if (getPlayerTurn().equals(players[0])) {
                return internalBoard.getPlayer1Pits();
            } else {
                return internalBoard.getPlayer2Pits();
            }
        }

        private int[] getOpponentPits() {
            if (getPlayerTurn().equals(players[0])) {
                return internalBoard.getPlayer2Pits();
            } else {
                return internalBoard.getPlayer1Pits();
            }
        }

        private BoardMancala getPlayerTurnMancala() {
            if (getPlayerTurn().equals(players[0])) {
                return internalBoard.getPlayer1Mancala();
            } else {
                return internalBoard.getPlayer2Mancala();
            }
        }

        private BoardMancala getOpponentMancala() {
            if (getPlayerTurn().equals(players[0])) {
                return internalBoard.getPlayer2Mancala();
            } else {
                return internalBoard.getPlayer1Mancala();
            }
        }

    }

}
