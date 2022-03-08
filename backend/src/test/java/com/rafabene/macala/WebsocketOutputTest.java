
package com.rafabene.macala;

import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Player;
import com.rafabene.mancala.web.output.WebsocketOutput;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
class WebsocketOutputTest {

        private Game game = Game.getInstance();

        private Player player1 = new Player("1");

        private Player player2 = new Player("2");

        private String sample_message = "Sample Message";

        @BeforeEach
        public void resetGame() throws IllegalGameStateException {
                game.getBoard().reset();
                game.stopGame();
                game.getPlayers()[0] = null;
                game.getPlayers()[1] = null;
                game.addNewPlayer(player1);
                game.addNewPlayer(player2);
                game.startGame();
                game.getBoard().getPlayer1Mancala().setContent(10);
                game.getBoard().getPlayer2Mancala().setContent(20);
        }

        @Test
        public void testMessage(){
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(sample_message, websocketOutput.getMessage(), "Published message should be the same");
        }

        @Test
        public void testCurrentPlayer(){
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(player1, websocketOutput.getCurrentPlayer(), "Should be player 1 turn");
        }


        @Test
        public void testViewer(){
                //Output for player 3
                WebsocketOutput websocketOutput = new WebsocketOutput("3", game, sample_message);
                Assertions.assertNotEquals(player1, websocketOutput.getCurrentPlayer(), "Player 3 should not be the current player");
        }


        @Test
        public void testCurrentPlayerMancala() throws IllegalGameStateException{
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(10, websocketOutput.getCurrentPlayerMancala(), "Mancala for player 1 should be 10");
        }

        @Test
        public void testOpponentMancalas() throws IllegalGameStateException{
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(20, websocketOutput.getOpponentPlayerMancala(), "Mancala for player 2 should be 20");
        }




}
