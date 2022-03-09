package com.rafabene.macala;

import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.GameStatus;
import com.rafabene.mancala.domain.IllegalGameMoveException;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@AddConfig(key = "stonesQuantity", value = "6")
@AddConfig(key = "pitsQuantity", value = "6")
public class GameRulesTest {

    private Game game = Game.getInstance();

    private Player player1 = new Player("1");

    private Player player2 = new Player("2");

    @BeforeEach
    public void resetGame() throws IllegalGameStateException {
        game.getBoard().reset();
        game.getPlayers()[0] = null;
        game.getPlayers()[1] = null;
        game.stopGame();

        game.addNewPlayer(player1);
        game.addNewPlayer(player2);
        game.startGame();
    }

    /**
     * Test if the value of the move is outside the range 1 to pitsQuantity
     * 
     * @throws IllegalGameStateException
     */
    @Test
    public void testInvalidPit() throws IllegalGameStateException {
        try {
            game.move(0);
            Assertions.fail("Can't move a pit starting at 0");
        } catch (IllegalGameMoveException e) {
            Assertions.assertNotNull(e, "Should fail to move pit 0");
        }
    }

    /**
     * Test if the value of the move is outside the range 1 to pitsQuantity
     * 
     * @throws IllegalGameStateException
     */
    @Test
    public void testInvalidPitHigher() throws IllegalGameStateException {
        try {
            game.move(7);
            Assertions.fail("Can't move a pit grether the number of pits");
        } catch (IllegalGameMoveException e) {
            Assertions.assertNotNull(e, "Should fail to move pit 7");
        }
    }

    /**
     * Test if the pit moved should be and the opponent received the stone
     * 
     * @throws IllegalGameStateException
     */
    @Test
    public void testMovedPitItShouldBe0() throws IllegalGameMoveException, IllegalGameStateException {
        game.move(6);
        Assertions.assertEquals(0, game.getBoard().getPlayer1Pits()[5], "Pit moved should be empty");
        Assertions.assertEquals(1, game.getBoard().getPlayer1Mancala().getContent(),
                "Macala should have it's first stone");
        Assertions.assertEquals(7, game.getBoard().getPlayer2Pits()[0], "Opponents pit should have a stone");
    }

    /**
     * Test if I can't move with 0 stones
     * 
     * @throws IllegalGameStateException
     */
    @Test
    @AddConfig(key = "stonesQuantity", value = "5")
    public void testCantMove0() throws IllegalGameMoveException, IllegalGameStateException {
        // Move all stones
        game.move(1);
        Assertions.assertEquals(6, game.getBoard().getPlayer1Pits()[1], "Pit 2 should be 6 after moving");
        // Try to move an empty Stone
        game.move(1);
        Assertions.assertEquals(0, game.getBoard().getPlayer1Pits()[0], "Pit should be empty");
        Assertions.assertEquals(6, game.getBoard().getPlayer1Pits()[1],
                "Pit 2 should be 6 after moving no stones to it");
    }

    /**
     * Test if It's other player's turn if the last stone is in his side
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    @AddConfig(key = "stonesQuantity", value = "5")
    public void testNextPlayerTurn() throws IllegalGameMoveException, IllegalGameStateException {
        game.move(1); // Last position in current's player pit
        Assertions.assertEquals(player1, game.getPlayerTurn(), "Player 1 should play again");

        game.getBoard().reset();
        game.move(2); // Last position in macala
        Assertions.assertEquals(player1, game.getPlayerTurn(), "Player 1 should play again");

        game.getBoard().reset();
        game.move(3); // Last positon in opponent's pit
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Player 2 should be the player now");
    }

    /**
     * Test if we don't place a stone in opponent's Macala
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    @AddConfig(key = "stonesQuantity", value = "10")
    public void testSkipOpponentsMacala() throws IllegalGameMoveException, IllegalGameStateException {
        game.move(6);
        Assertions.assertEquals(0, game.getBoard().getPlayer1Pits()[5], "Pit 6 should be 0");
        Assertions.assertEquals(1, game.getBoard().getPlayer1Mancala().getContent(), "Player 1 Mancala should be 1");
        Assertions.assertEquals(11, game.getBoard().getPlayer2Pits()[5], "Player 2 pit should be 9");
        Assertions.assertEquals(0, game.getBoard().getPlayer2Mancala().getContent(), "Player 2 Mancala should be 0");
        Assertions.assertEquals(11, game.getBoard().getPlayer1Pits()[0], "Player 1 Pit should reive a stone");
        Assertions.assertEquals(11, game.getBoard().getPlayer1Pits()[1], "Player 1 Pit should reive a stone");
        Assertions.assertEquals(11, game.getBoard().getPlayer1Pits()[2], "Player 1 Pit should reive a stone");
    }

    /**
     * Test if don't capture stones if the last stone is in opponent's side
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    public void testCaptureLastStoneInOpponent() throws IllegalGameMoveException, IllegalGameStateException {
        game.move(1);
        Assertions.assertEquals(player1, game.getPlayerTurn(), "Player 1 should play again");
        game.move(6);
        Assertions.assertEquals(0, game.getBoard().getPlayer1Pits()[0],
                "Player 1[Pit 1] should be empty for the next test");
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Player 2 should be the player now");
        game.move(1);
        Assertions.assertEquals(1, game.getBoard().getPlayer1Pits()[0], "Player 1[Pit 1] should have 1 stone");

    }

    /**
     * Test if we capture stones if the last stone is in current's player side
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    @AddConfig(key = "stonesQuantity", value = "13")
    public void testCaptureLastStoneInCurrentPlayer() throws IllegalGameMoveException, IllegalGameStateException {
        game.move(1);
        Assertions.assertEquals(15, game.getBoard().getPlayer1Pits()[0],
                "Player 1[Pit 1] should have the captured stones");
        Assertions.assertEquals(0, game.getBoard().getPlayer2Pits()[5],
                "Player 2[Pit 6] should have no stones (it has been captured");
        Assertions.assertEquals(player1, game.getPlayerTurn(), "Player 1 should play again");
    }

    /**
     * Test Game Over
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    @AddConfig(key = "stonesQuantity", value = "1")
    public void testGameOver() throws IllegalGameMoveException, IllegalGameStateException {
        game.move(1);
        game.move(2);
        game.move(3);
        game.move(4);
        game.move(5);
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Should be player 2");
        game.move(6);
        game.move(5);
        game.move(4);
        game.move(3);
        game.move(2);
        game.move(1);
        // Second round same player 2
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Should be player 2");
        game.move(6);
        game.move(5);
        game.move(4);
        game.move(3);
        game.move(2);
        // Third round same player 2
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Should be player 2");
        game.move(6);
        game.move(5);
        game.move(4);
        game.move(3);
        // Fourth round same player 2
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Should be player 2");
        game.move(6);
        game.move(5);
        game.move(4);
        // Fifth round same player 2
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Should be player 2");
        game.move(6);
        game.move(5);
        // Sixth round same player 2
        Assertions.assertEquals(player2, game.getPlayerTurn(), "Should be player 2");
        game.move(6);
        Assertions.assertEquals(GameStatus.GAME_OVER, game.getGameStatus(), "Game should be OVER");
        Assertions.assertEquals(player2, game.getWinner(), "Player 2 should be the winner");
    }

}
