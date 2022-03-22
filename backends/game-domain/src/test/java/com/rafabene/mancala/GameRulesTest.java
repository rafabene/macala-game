package com.rafabene.mancala;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.GameConfiguration;
import com.rafabene.mancala.domain.GameStatus;
import com.rafabene.mancala.domain.IllegalGameMoveException;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Player;

import org.junit.Before;
import org.junit.Test;

public class GameRulesTest {

    private Game game;

    private Player player1 = new Player("1");

    private Player player2 = new Player("2");

    @Before
    public void resetGame() throws IllegalGameStateException {
        game = new Game();
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
            fail("Can't move a pit starting at 0");
        } catch (IllegalGameMoveException e) {
            assertNotNull("Should fail to move pit 0", e);
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
            fail("Can't move a pit grether the number of pits");
        } catch (IllegalGameMoveException e) {
            assertNotNull("Should fail to move pit 7", e);
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
        assertEquals("Pit moved should be empty", 0, game.getBoard().getPlayer1Pits()[5]);
        assertEquals("Macala should have it's first stone", 1, game.getBoard().getPlayer1Mancala().getContent());
        assertEquals("Opponents pit should have a stone", 7, game.getBoard().getPlayer2Pits()[0]);
    }

    /**
     * Test if I can't move with 0 stones
     * 
     * @throws IllegalGameStateException
     */
    @Test
    public void testCantMove0() throws IllegalGameMoveException, IllegalGameStateException {
        game = new Game(new GameConfiguration(6, 5));
        game.addNewPlayer(player1);
        game.addNewPlayer(player2);
        game.startGame();
        // Move all stones
        game.move(1);
        assertEquals( "Pit 2 should be 6 after moving", 6, game.getBoard().getPlayer1Pits()[1]);
        // Try to move an empty Stone
        game.move(1);
        assertEquals( "Pit should be empty", 0, game.getBoard().getPlayer1Pits()[0]);
        assertEquals("Pit 2 should be 6 after moving no stones to it", 6, game.getBoard().getPlayer1Pits()[1]);
    }

    /**
     * Test if It's other player's turn if the last stone is in his side
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    public void testNextPlayerTurn() throws IllegalGameMoveException, IllegalGameStateException {
        game = new Game(new GameConfiguration(6, 5));
        game.addNewPlayer(player1);
        game.addNewPlayer(player2);
        game.startGame();
        game.move(1); // Last position in current's player pit
        assertEquals("Player 1 should play again", player1, game.getPlayerTurn());

        game.getBoard().reset();
        game.move(2); // Last position in macala
        assertEquals("Player 1 should play again", player1, game.getPlayerTurn());

        game.getBoard().reset();
        game.move(3); // Last positon in opponent's pit
        assertEquals("Player 2 should be the player now", player2, game.getPlayerTurn());
    }

    /**
     * Test if we don't place a stone in opponent's Macala
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    public void testSkipOpponentsMacala() throws IllegalGameMoveException, IllegalGameStateException {
        game = new Game(new GameConfiguration(6, 10));
        game.addNewPlayer(player1);
        game.addNewPlayer(player2);
        game.startGame();
        game.move(6);
        assertEquals("Pit 6 should be 0", 0, game.getBoard().getPlayer1Pits()[5]);
        assertEquals("Player 1 Mancala should be 1", 1, game.getBoard().getPlayer1Mancala().getContent());
        assertEquals("Player 2 pit should be 9", 11, game.getBoard().getPlayer2Pits()[5]);
        assertEquals("Player 2 Mancala should be 0", 0, game.getBoard().getPlayer2Mancala().getContent() );
        assertEquals("Player 1 Pit should reive a stone", 11, game.getBoard().getPlayer1Pits()[0]);
        assertEquals("Player 1 Pit should reive a stone", 11, game.getBoard().getPlayer1Pits()[1]);
        assertEquals("Player 1 Pit should reive a stone", 11, game.getBoard().getPlayer1Pits()[2]);
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
        assertEquals("Player 1 should play again", player1, game.getPlayerTurn());
        game.move(6);
        assertEquals("Player 1[Pit 1] should be empty for the next test", 0, game.getBoard().getPlayer1Pits()[0]);
        assertEquals("Player 2 should be the player now", player2, game.getPlayerTurn());
        game.move(1);
        assertEquals("Player 1[Pit 1] should have 1 stone", 1, game.getBoard().getPlayer1Pits()[0]);

    }

    /**
     * Test if we capture stones if the last stone is in current's player side
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    public void testCaptureLastStoneInCurrentPlayer() throws IllegalGameMoveException, IllegalGameStateException {
        game = new Game(new GameConfiguration(6, 13));
        game.addNewPlayer(player1);
        game.addNewPlayer(player2);
        game.startGame();
        game.move(1);
        assertEquals("Player 1[Pit 1] should have the captured stones", 15, game.getBoard().getPlayer1Pits()[0]);
        assertEquals("Player 2[Pit 6] should have no stones (it has been captured", 0, game.getBoard().getPlayer2Pits()[5]);
        assertEquals("Player 1 should play again", player1, game.getPlayerTurn());
    }

    /**
     * Test Game Over
     * 
     * @throws IllegalGameMoveException
     * @throws IllegalGameStateException
     */
    @Test
    public void testGameOver() throws IllegalGameMoveException, IllegalGameStateException {
        game = new Game(new GameConfiguration(6, 1));
        game.addNewPlayer(player1);
        game.addNewPlayer(player2);
        game.startGame();
        game.move(1);
        game.move(2);
        game.move(3);
        game.move(4);
        game.move(5);
        assertEquals("Should be player 2", player2, game.getPlayerTurn());
        game.move(6);
        game.move(5);
        game.move(4);
        game.move(3);
        game.move(2);
        game.move(1);
        // Second round same player 2
        assertEquals( "Should be player 2", player2, game.getPlayerTurn());
        game.move(6);
        game.move(5);
        game.move(4);
        game.move(3);
        game.move(2);
        // Third round same player 2
        assertEquals("Should be player 2", player2, game.getPlayerTurn());
        game.move(6);
        game.move(5);
        game.move(4);
        game.move(3);
        // Fourth round same player 2
        assertEquals("Should be player 2", player2, game.getPlayerTurn());
        game.move(6);
        game.move(5);
        game.move(4);
        // Fifth round same player 2
        assertEquals("Should be player 2", player2, game.getPlayerTurn());
        game.move(6);
        game.move(5);
        // Sixth round same player 2
        assertEquals("Should be player 2", player2, game.getPlayerTurn());
        game.move(6);
        assertEquals("Game should be OVER", GameStatus.GAME_OVER, game.getGameStatus());
        assertEquals( "Player 2 should be the winner", player2, game.getWinner());
    }
}
