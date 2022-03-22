package com.rafabene.mancalagame;

import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Player;
import com.rafabene.mancalagame.web.output.WebsocketOutput;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MancalaGameApplicationTests {

	private Game game = new Game();

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
	void contextLoads() {
	}

	    /**
         * Test with we are able to get a message from the output
         */
        @Test
        public void testMessage(){
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(sample_message, websocketOutput.getMessage(), "Published message should be the same");
        }

        /**
         * Test if the current player matche's the number of the session output
         */
        @Test
        public void testCurrentPlayer(){
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(player1, websocketOutput.getCurrentPlayer(), "Should be player 1 turn");
        }


        /**
         * Test if  the Current player is not the websocker viewer
         */
        @Test
        public void testViewer(){
                //Output for player 3
                WebsocketOutput websocketOutput = new WebsocketOutput("3", game, sample_message);
                Assertions.assertNotEquals(player1, websocketOutput.getCurrentPlayer(), "Player 3 should not be the current player");
        }


        /**
         * Test if the currentPlayer is based on the session number
         * 
         * @throws IllegalGameStateException
         */
        @Test
        public void testCurrentPlayerMancala() throws IllegalGameStateException{
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(10, websocketOutput.getCurrentPlayerMancala(), "Mancala for player 1 should be 10");
        }
        
        /**
         * Test if the opponent player is based on the session number
         * 
         * @throws IllegalGameStateException
         */
        @Test
        public void testOpponentMancalas() throws IllegalGameStateException{
                WebsocketOutput websocketOutput = new WebsocketOutput("1", game, sample_message);
                Assertions.assertEquals(20, websocketOutput.getOpponentPlayerMancala(), "Mancala for player 2 should be 20");
        }



}
