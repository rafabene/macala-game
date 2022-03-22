package com.rafabene.mancala.web.output;

import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.Player;

/**
 * Representes the output from the websockt to the game
 */
public class WebsocketOutput {

    // The game instance
    private Game game;

    // The message to be displayed to the Websocket session
    private String message;

    // The current Websocket session Id
    private String currentSessionId;

    public WebsocketOutput(String sessionId, Game game, String message) {
        this.currentSessionId = sessionId;
        this.game = game;
        this.message = message;
    }

    public Game getGame() {
        return this.game;
    }

    public String getMessage() {
        return this.message;
    }

    public int[] getCurrentPlayerPits() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.equals(game.getPlayers()[0])) {
            return game.getBoard().getPlayer1Pits();
        } else {
            return game.getBoard().getPlayer2Pits();
        }
    }

    public int getCurrentPlayerMancala() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.equals(game.getPlayers()[0])) {
            return game.getBoard().getPlayer1Mancala().getContent();
        } else {
            return game.getBoard().getPlayer2Mancala().getContent();
        }
    }

    public int[] getOpponentPlayerPits() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.equals(game.getPlayers()[0])) {
            return game.getBoard().getPlayer2Pits();
        } else {
            return game.getBoard().getPlayer1Pits();
        }
    }

    public int getOpponentPlayerMancala() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.equals(game.getPlayers()[0])) {
            return game.getBoard().getPlayer2Mancala().getContent();
        } else {
            return game.getBoard().getPlayer1Mancala().getContent();
        }
    }

    public Player getCurrentPlayer() {
        for (Player player : game.getPlayers()) {
            if (player !=null && player.getGamerId().equals(currentSessionId)) {
                return player;
            }
        }
        return null;
    }
    
}
