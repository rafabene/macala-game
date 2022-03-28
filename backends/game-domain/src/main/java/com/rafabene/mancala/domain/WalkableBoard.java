package com.rafabene.mancala.domain;

import java.util.logging.Logger;

/**
 * 
 * An Walkable board contains an array in the following format:
 * 
 * [p1, p1, p1, p1, mancala1, p2, p2, p2, p2]
 * 
 * It's formed by player1Pits + player1Mancala + player2Pits
 * 
 * This instance should be used for each move. 
 * 
 * This class is just a helper class, thus it's not available 
 * externally to the game domain.
 * 
 */
class WalkableBoard {

    private Logger logger = Logger.getLogger(this.getClass().toString());

    private int[] walkableBoard;

    private Board board;

    private Player currentPlayer;

    private Player[] players;

    private boolean moved;

    /**
     * Creates an Walkable board using the information from the
     * board, players and the current player.
     */
    public WalkableBoard(Board board, Player[] players, Player opponnentPlayer) {
        this.board = board;
        this.players = players;
        this.currentPlayer = opponnentPlayer;
        int pitsQuantity = board.getPlayer1Pits().length;
        walkableBoard = new int[(pitsQuantity * 2) + 1];
        for (int x = 0; x < pitsQuantity; x++) {
            walkableBoard[x] = getPlayerTurnPits()[x].getNumberOfStones();
            // +1 is needed to skip Player's Mancala on the last position
            walkableBoard[x + pitsQuantity + 1] = getOpponentPits()[x].getNumberOfStones();
        }
        walkableBoard[pitsQuantity] = getPlayerTurnMancala().getContent();
    }

    /**
     * Seed the stones in the pit informed in the parameter.
     * 
     * @param pit position of the pit: 1 to pitsQuantity
     * @return true if the last positon is in current's player board
     * @throws IllegalGameMoveException if the pit informed is greatter than
     *                                  pitsQuantiy.
     * @throws IllegalGameStateException if you try to reuse this game instance to move twice.
     */
    public boolean move(int pit) throws IllegalGameMoveException, IllegalGameStateException {
        if (moved){
            throw new IllegalGameStateException("You can't reuse this WalkableBoard. Please, create another instance.");
        }
        int pitsQuantity = board.getPlayer1Pits().length;
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
        while (stones > 0) {
            // Go to next pit
            pitPosition++;

            // Circular position
            walkableBoardPosition = (int) (pitPosition) % walkableBoard.length;

            // Add a stone to the Pit
            walkableBoard[walkableBoardPosition]++;
            stones--;

        }
        fillInternalBoard();

        logger.info(String.format("Last WalkablebBoard position: %s - Is this in current's player side: %s ",
                walkableBoardPosition, isThisPositionInCurrentsPlayerSide(walkableBoardPosition)));
        /*
         * If it's in current's player side,
         * and it's not the Mancala
         * and we have one stone
         */
        if (isThisPositionInCurrentsPlayerSide(walkableBoardPosition)
                && walkableBoardPosition != pitsQuantity
                && walkableBoard[walkableBoardPosition] == 1) {
            captureOpponentStones(walkableBoardPosition);
        }
        this.moved = true;
        return isThisPositionInCurrentsPlayerSide(walkableBoardPosition);
    }

    // Return true if we are in our side of the board after moving (pits + mancala)
    private boolean isThisPositionInCurrentsPlayerSide(int walkableBoardPosition) {
        return walkableBoardPosition <= board.getPlayer1Pits().length;
    }

    private void captureOpponentStones(int walkableBoardPosition) {
        logger.info("Capturing opponent's stones. Last position: " + walkableBoardPosition);
        int currentPlayerLastPosition = walkableBoardPosition;
        int offset = getOpponentPits().length - 1 - currentPlayerLastPosition;
        int stonesInOpponent = getOpponentPits()[offset].getNumberOfStones();
        logger.info("Stones in Opponent's pit: " + stonesInOpponent);
        getOpponentPits()[offset].setNumberOfStones(0);
        int playerStones = getPlayerTurnPits()[currentPlayerLastPosition].getNumberOfStones();
        getPlayerTurnPits()[currentPlayerLastPosition].setNumberOfStones(playerStones + stonesInOpponent);
    }

    /**
     * Get the walkable board in the format of
     * [p1, p1, p1, p1, mancala1, p2, p2, p2, p2, mancala2]
     * 
     * and place the proper falues in the fields of the internal Board.
     */
    private void fillInternalBoard() {
        int pitsQuantity = board.getPlayer1Pits().length;
        for (int x = 0; x < pitsQuantity; x++) {
            getPlayerTurnPits()[x].setNumberOfStones(walkableBoard[x]);
            // +1 to skip Player's Mancala
            getOpponentPits()[x].setNumberOfStones(walkableBoard[x + pitsQuantity + 1]);
        }
        // Player Mancala
        getPlayerTurnMancala().setContent(walkableBoard[pitsQuantity]);
    }

    private Pit[] getPlayerTurnPits() {
        if (currentPlayer.equals(players[0])) {
            return board.getPlayer1Pits();
        } else {
            return board.getPlayer2Pits();
        }
    }

    private Pit[] getOpponentPits() {
        if (currentPlayer.equals(players[0])) {
            return board.getPlayer2Pits();
        } else {
            return board.getPlayer1Pits();
        }
    }

    private Mancala getPlayerTurnMancala() {
        if (currentPlayer.equals(players[0])) {
            return board.getPlayer1Mancala();
        } else {
            return board.getPlayer2Mancala();
        }
    }

}
