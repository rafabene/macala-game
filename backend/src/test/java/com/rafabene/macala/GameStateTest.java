
package com.rafabene.macala;

import javax.inject.Inject;

import com.rafabene.mancala.domain.Board;
import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.GameStatus;
import com.rafabene.mancala.domain.IllegalGameStageException;
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

        @Test
        public void testNoPlayers() {
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should start as not running");
                Assertions.assertNull(game.getPlayers()[0], "Game tests should not have players");
                Assertions.assertNull(game.getPlayers()[1], "Game tests should not have players");
                Assertions.assertNull(game.getPlayerTurn(), "Game tests should not have someone's turn");
        }

        @Test
        public void testStartWithoutPlayers() {
                try {
                        game.startGame();
                        Assertions.fail("Can't start the game");
                } catch (IllegalGameStageException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running");
        }

        @Test
        public void testStartWith1Player() {
                try {
                        game.addNewPlayer(player1);
                        game.startGame();
                        Assertions.fail("Can't start the game");
                } catch (IllegalGameStageException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running");
        }

        @Test
        public void testStartWith2Players() {
                try {
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        game.startGame();
                } catch (IllegalGameStageException e) {
                        Assertions.fail("Could not fail");
                }
                Assertions.assertEquals(GameStatus.RUNNING, game.getGameStatus(), "Game should be running");
        }

        @Test
        public void testStartWith2PlayersTwice() {
                try {
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        game.startGame();
                        game.startGame();
                        Assertions.fail("Can't start twice");
                } catch (IllegalGameStageException e) {
                        // Expected to Fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.RUNNING, game.getGameStatus(), "Game should be running even starting twice");
        }

        public void addThirdPlayer() {
                try {
                        game.addNewPlayer(player1);
                        game.addNewPlayer(player2);
                        game.addNewPlayer(player3);
                        Assertions.fail("Can't add a third player");
                } catch (IllegalGameStageException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running");
        }

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
                } catch (IllegalGameStageException e) {
                        // Expected to fail
                        Assertions.assertNotNull(e);
                }
                Assertions.assertEquals(GameStatus.NOT_RUNNING, game.getGameStatus(), "Game should not be running after stoped");
        }
}
