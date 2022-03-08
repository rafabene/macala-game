package com.rafabene.mancala.domain;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class Game {

    private Player[] players = new Player[2];

    private Board board = new Board();

    private GameStatus gameStatus = GameStatus.NOT_RUNNING;

    private static Game instance;

    private Player playerTurn;

    // Can't be instantiated
    private Game() {

    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void startGame() throws IllegalGameStageException {
        if (gameStatus.equals(GameStatus.RUNNING)) {
            throw new IllegalGameStageException("Game is already running!");
        }
        if (players[0] != null && players[1] != null) {
            board.reset();
            playerTurn = players[0];
            gameStatus = GameStatus.RUNNING;
        } else {
            throw new IllegalGameStageException("We can't start the game without 2 players");
        }
    }

    public void stopGame() {
        gameStatus = GameStatus.NOT_RUNNING;
        playerTurn = null;
    }

    public void reset() {
        playerTurn = players[0];
        board.reset();
    }

    public void move(int pit) {
        WalkableBoard walkableBoard = new WalkableBoard();
        walkableBoard.move(pit);
        walkableBoard.fillBoard();
        changePlayerTurn();
    }

    private void changePlayerTurn() {
        if (playerTurn.equals(players[0])) {
            playerTurn = players[1];
        } else {
            playerTurn = players[0];
        }
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
     * An Walkable board contains an array in the following format:
     * 
     * [p1, p1, p1, p1, macala1, p2, p2, p2, p2, macala2]
     * 
     * It's formed by player1Pits + playerMacala + player2Pits + player2Mancala
     * 
     * This instance should be used for each move
     * 
     */
    class WalkableBoard {

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
                walkableBoard[x + pitsQuantity + 1] = getOtherPlayerPits()[x];
            }
            walkableBoard[pitsQuantity] = getPlayerTurnMancala().getContent();
            // Last positon
            walkableBoard[walkableBoard.length - 1] = getOtherPlayerMancala().getContent();
        }

        public void move(int pit) {
            int pitPosition = pit - 1;
            int stones = walkableBoard[pitPosition];
            walkableBoard[pitPosition] = 0;
            for (int x = 1; x <= stones; x++) {
                // Circular position
                int walkableBoardPosition = (int) (pitPosition + x) % walkableBoard.length;
                // Skip other player's Mancala
                if (walkableBoardPosition != walkableBoard.length - 1) {
                    walkableBoard[walkableBoardPosition]++;
                }
            }

        }

        private void fillBoard() {
            int pitsQuantity = getPitsQuantity();
            for (int x = 0; x < pitsQuantity; x++) {
                getPlayerTurnPits()[x] = walkableBoard[x];
                // +1 to skip Player's Mancala
                getOtherPlayerPits()[x] = walkableBoard[x + pitsQuantity + 1];
            }
            getPlayerTurnMancala().setContent(walkableBoard[pitsQuantity]);
            // Last position
            getOtherPlayerMancala().setContent(walkableBoard[(walkableBoard.length - 1)]);
        }

        private int[] getPlayerTurnPits() {
            if (getPlayerTurn().equals(players[0])) {
                return board.getPlayer1Pits();
            } else {
                return board.getPlayer2Pits();
            }
        }

        private int[] getOtherPlayerPits() {
            if (getPlayerTurn().equals(players[0])) {
                return board.getPlayer2Pits();
            } else {
                return board.getPlayer1Pits();
            }
        }

        private Mancala getPlayerTurnMancala() {
            if (getPlayerTurn().equals(players[0])) {
                return board.getPlayer1Mancala();
            } else {
                return board.getPlayer2Mancala();
            }
        }

        private Mancala getOtherPlayerMancala() {
            if (getPlayerTurn().equals(players[0])) {
                return board.getPlayer2Mancala();
            } else {
                return board.getPlayer1Mancala();
            }
        }

    }

}
