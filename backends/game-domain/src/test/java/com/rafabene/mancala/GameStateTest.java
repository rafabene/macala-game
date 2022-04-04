package com.rafabene.mancala;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.rafabene.mancala.domain.Board;
import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.GameStatus;
import com.rafabene.mancala.domain.IllegalGameMoveException;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Pit;
import com.rafabene.mancala.domain.Player;

import org.junit.Before;
import org.junit.Test;

public class GameStateTest {

        private Game game = new Game();

        private Player player1 = new Player("1");

        private Player player2 = new Player("2");

        private Player player3 = new Player("3");

        @Before
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
                assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus());
                assertNotNull("Board should exist", game.getBoard());
                Board board = game.getBoard();
                assertEquals("Player 1 Macala should be 0", 0, board.getPlayer1Mancala().getContent());
                assertEquals("Player 2 Macala should be 0", 0, board.getPlayer2Mancala().getContent());
                Pit[] player1Pits = board.getPlayer1Pits();
                Pit[] player2Pits = board.getPlayer1Pits();
                int pitsQuantity = game.getGameConfiguration().getNumberOfPits();
                int stonesQuantity = game.getGameConfiguration().getNumberOfStones();
                assertEquals("Player 1 Macala should have the right number of pits", pitsQuantity, player1Pits.length);
                assertEquals("Player 2 Macala should have the right number of pits", pitsQuantity, player2Pits.length);
                for (int i = 0; i < pitsQuantity; i++) {
                        assertEquals("Player 1 pits should have the right number of stones", stonesQuantity,
                                        player1Pits[i].getNumberOfStones());
                        assertEquals("Player 2 pits should have the right number of stones", stonesQuantity,
                                        player2Pits[i].getNumberOfStones());
                }
        }

        /**
         * Test if the game starts with no player's and player turn.
         */
        @Test
        public void testNoPlayers() {
                assertEquals("Game should start as not running", GameStatus.NOT_RUNNING, game.getGameStatus());
                assertNull("Game tests should not have players", game.getPlayers()[0]);
                assertNull("Game tests should not have players", game.getPlayers()[1]);
                assertNull("Game tests should not have someone's turn", game.getPlayerTurn());
                assertNull("Game tests should not have a Winner yet", game.getWinner());
        }

        /**
         * Test if we can't start the game without players
         */
        @Test
        public void testStartWithoutPlayers() {
                try {
                        game.startGame();
                        fail("Can't start the game");
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        assertNotNull(e);
                }
                assertEquals("Game should not be running", GameStatus.NOT_RUNNING, game.getGameStatus());
        }

        /**
         * Test if we can't start the game with just one player.
         */
        @Test
        public void testStartWith1Player() {
                try {
                        game.addNewPlayer(player1);
                        game.startGame();
                        fail("Can't start the game");
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        assertNotNull(e);
                }
                assertEquals("Game should not be running", GameStatus.NOT_RUNNING, game.getGameStatus());
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
                        fail("Could not fail");
                }
                assertEquals("Game should be running", GameStatus.RUNNING, game.getGameStatus());
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
                        fail("Can't start twice");
                } catch (IllegalGameStateException e) {
                        // Expected to Fail
                        assertNotNull(e);
                }
                assertEquals("Game should be running even starting twice", GameStatus.RUNNING, game.getGameStatus());
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
                        fail("Can't add a third player");
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        assertNotNull(e);
                }
                assertEquals("Game should not be running", GameStatus.NOT_RUNNING, game.getGameStatus());
        }

        /**
         * Test if the next player is correct after starting and stopping the game
         */
        @Test
        public void testPlayerTurn() {
                try {
                        assertNull("We don't have someone's turn for a stoped game", game.getPlayerTurn());
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        assertNull("We don't have someone's turn even if we have players, but the game is stoped",
                                        game.getPlayerTurn());
                        game.startGame();
                        assertEquals("Player 1 starts the game", player1, game.getPlayerTurn());
                        game.stopGame();
                        assertNull("We don't have someone's turn for a stoped game", game.getPlayerTurn());
                } catch (IllegalGameStateException e) {
                        // Expected to fail
                        assertNotNull(e);
                }
                assertEquals("Game should not be running after stoped", GameStatus.NOT_RUNNING, game.getGameStatus());
        }

        /**
         * Test if we can't move a piece with the game not running
         */
        @Test
        public void testGameMoveWithoutStart() {
                try {
                        game.move(1);
                        fail("Should not be able to move. Game is stopped");
                } catch (IllegalGameMoveException | IllegalGameStateException e) {
                        assertNotNull("Move not allowed. Game is stopped", e);
                }
        }

        /**
         * Test if we can't move a piece with the game not running
         */
        @Test
        public void testResetState() {
                try {
                        game.move(1);
                        assertEquals("Pit 1 should be 0", 0, game.getBoard().getPlayer1Pits()[0].getNumberOfStones());
                        assertEquals("Player 1 Macala should be 1", 1,
                                        game.getBoard().getPlayer1Mancala().getContent());
                        game.getBoard().reset();
                        assertEquals("Pit 1 should be 6", 6, game.getBoard().getPlayer1Pits()[0].getNumberOfStones());
                        assertEquals("Player 1 Macala should be 0", 0,
                                        game.getBoard().getPlayer1Mancala().getContent());
                } catch (IllegalGameMoveException | IllegalGameStateException e) {
                        assertNotNull("Move not allowed. Game is stopped", e);
                }
        }
}
