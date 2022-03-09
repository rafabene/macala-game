
package com.rafabene.macala;

import javax.inject.Inject;

import com.rafabene.mancala.domain.Board;
import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.GameStatus;
import com.rafabene.mancala.domain.IllegalGameMoveException;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Player;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
class GameStateTest {

        @Inject
        @ConfigProperty(name = "stonesQuantity")
        private int stonesQuantity;

        @Inject
        @ConfigProperty(name = "pitsQuantity")
        private int pitsQuantity;

        private Game game = Game.getInstance();

        private Player player1 = new Player("1");

        private Player player2 = new Player("2");

        private Player player3 = new Player("3");

        @BeforeEach
        public void resetGame() {
                game.getBoard().reset();
                game.getPlayers()[0] = null;
                game.getPlayers()[1] = null;
                game.stopGame();
        }

        /**
         * Test if the board has the right (zeroed) state in the beggining of the game
         */
        @Test
        public void testGameNotRunning() {
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus());
                Assertions.assertNotNull(game.getBoard(), "Board should exist");
                Board board = game.getBoard();
                Assertions.assertEquals(0, board.getPlayer1Mancala().getContent(), "Player 1 Macala should be 0");
                Assertions.assertEquals(0, board.getPlayer2Mancala().getContent(), "Player 2 Macala should be 0");
                int[] player1Pits = board.getPlayer1Pits();
                int[] player2Pits = board.getPlayer1Pits();
                Assertions.assertEquals(pitsQuantity, player1Pits.length, "Player 1 Macala should have the right number of pits");
                Assertions.assertEquals(pitsQuantity, player2Pits.length, "Player 2 Macala should have the right number of pits");
                for (int i = 0; i < pitsQuantity; i++) {
                        Assertions.assertEquals(stonesQuantity, player1Pits[i], "Player 1 pits should have the right number of stones");
                        Assertions.assertEquals(stonesQuantity, player2Pits[i], "Player 2 pits should have the right number of stones");
                }
        }

        /**
         * Test if the game starts with no player's and player turn.
         */
        @Test
        public void testNoPlayers() {
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should start as not running");
                Assertions.assertNull(game.getPlayers()[0], "Game tests should not have players");
                Assertions.assertNull(game.getPlayers()[1], "Game tests should not have players");
                Assertions.assertNull(game.getPlayerTurn(), "Game tests should not have someone's turn");
                Assertions.assertNull(game.getWinner(), "Game tests should not have a Winner yet");
        }

        /**
         * Test if we can't start the game without players
         */
        @Test
        public void testStartWithoutPlayers() {
                try {
                        game.startGame();
                        Assertions.fail("Can't start the game");
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running");
        }

        /**
         * Test if we can't start the game with just one player.
         */
        @Test
        public void testStartWith1Player() {
                try {
                        game.addNewPlayer(player1);
                        game.startGame();
                        Assertions.fail("Can't start the game");
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running");
        }


        /**
         * Test if we can only start the game with two players
         */
        @Test
        public void testStartWith2Players() {
                try {
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        game.startGame();
                } catch (IllegalGameStateException e) {
                        Assertions.fail("Could not fail");
                }
                Assertions.assertEquals(GameStatus.RUNNING, game.getGameStatus(), "Game should be running");
        }

        /**
         * Test if we can't start the game twice
         */
        @Test
        public void testStartWith2PlayersTwice() {
                try {
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        game.startGame();
                        game.startGame();
                        Assertions.fail("Can't start twice");
                } catch (IllegalGameStateException e) {
                        // Expected to Fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.RUNNING, game.getGameStatus(), "Game should be running even starting twice");
        }


        /**
         * Test if we can't add a third player
         */
        @Test
        public void addThirdPlayer() {
                try {
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        game.addNewPlayer(player3);
                        Assertions.fail("Can't add a third player");
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running");
        }

        /**
         * Test if the next player is correct after starting and stopping the game
         */
        @Test
        public void testPlayerTurn(){
                try {
                        Assertions.assertNull(game.getPlayerTurn(), "We don't have someone's turn for a stoped game");
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        Assertions.assertNull(game.getPlayerTurn(), "We don't have someone's turn even if we have players, but the game is stoped");
                        game.startGame();
                        Assertions.assertEquals(player1, game.getPlayerTurn(), "Player 1 starts the game");
                        game.stopGame();
                        Assertions.assertNull(game.getPlayerTurn(), "We don't have someone's turn for a stoped game");
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running after stoped");
        }

        /**
         * Test if we can't move a piece with the game not running
         */
        @Test
        public void testGameMoveWithoutStart(){
                try {
                        game.move(1);
                        Assertions.fail("Should not be able to move. Game is stopped");
                } catch (IllegalGameMoveException | IllegalGameStateException e) {
                        Assertions.assertNotNull(e, "Move not allowed. Game is stopped");
                }
        }
}
