package com.rafabene.mancala.domain;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class Game {

    private Logger logger = Logger.getLogger(this.getClass().toString());

    private Player[] players = new Player[2];

    private Board board = new Board();

    private GameStatus gameStatus = GameStatus.NOT_RUNNING;

    private static Game instance;

    private Player playerTurn;


    // Can't be instantiated
    private Game() {

    }

    public void reset(){
        playerTurn = players[0];
        board.reset();
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

    public void stopGame(){
        gameStatus = GameStatus.NOT_RUNNING;
        playerTurn = null;
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

    public void move(int pit){
        int pitPosition = pit - 1;
        int stones = getPlayerTurnPits()[pitPosition];
        getPlayerTurnPits()[pitPosition] = 0;
        int[] walkableBoard = getWalkableBoard(); 
        for (int x = 1; x <= stones; x++){
            // Circular position
            int walkableBoardPosition = (int) (pitPosition + x) % walkableBoard.length;
            // Skip other player's Mancala
            if (walkableBoardPosition != walkableBoard.length - 1){
                walkableBoard[walkableBoardPosition]++;
            }
        }
        fillBoard(walkableBoard);
        changePlayerTurn();
    }


    private void changePlayerTurn() {
        if (playerTurn.equals(players[0])){
            playerTurn = players[1];
        }else{
            playerTurn = players[0];
        }
    }

    private void fillBoard(int[] walkableBoard) {
        logger.info(Arrays.toString(walkableBoard));
        int pitsQuantity = getPitsQuantity();
        for(int x = 0; x < pitsQuantity; x++){
            getPlayerTurnPits()[x] = walkableBoard[x];
            //  +1 to skip Player's  Mancala
            getOtherPlayerPits()[x] = walkableBoard[x + pitsQuantity + 1];
        }
        getPlayerTurnMancala().setContent(walkableBoard[pitsQuantity]);
        getOtherPlayerMancala().setContent(walkableBoard[(walkableBoard.length - 1)]);
    }

    /**
     * 
     * An Walkable board is an array in the following format:
     * 
     * [p1, p1, p1, p1, macala1, p2, p2, p2, p2, macala2]
     * 
     * It's formed by player1Pits + playerMacala + player2Pits + player2Mancala
     * @return
     */
    @JsonbTransient
    public int[] getWalkableBoard() {
        int pitsQuantity = getPitsQuantity();
        int[] walkableBoard = new int[(pitsQuantity  * 2) + 2];
        for(int x = 0; x < pitsQuantity; x++){
            walkableBoard[x] = getPlayerTurnPits()[x];
            // +1 is needed to skip Player's Mancala on the last position
            walkableBoard[x + pitsQuantity + 1] = getOtherPlayerPits()[x];
        }
        walkableBoard[pitsQuantity] = getPlayerTurnMancala().getContent();
        // Last positon
        walkableBoard[walkableBoard.length - 1] = getOtherPlayerMancala().getContent();
        logger.info(Arrays.toString(walkableBoard));
        return walkableBoard;
    }


    private int getPitsQuantity(){
        Config config = ConfigProvider.getConfig();
        return config.getValue("pitsQuantity", Integer.class);
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
