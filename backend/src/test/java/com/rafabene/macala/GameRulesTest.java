package com.rafabene.macala;

import com.rafabene.mancala.domain.Game;
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

    @Test
    public void testInvalidPit() throws IllegalGameStateException {
        try {
            game.move(0);
            Assertions.fail("Can't move a pit starting at 0");
        } catch (IllegalGameMoveException e) {
            Assertions.assertNotNull(e, "Should fail to move pit 0");
        }
    }

    @Test
    public void testInvalidPitHigher() throws IllegalGameStateException {
        try {
            game.move(7);
            Assertions.fail("Can't move a pit grether the number of pits");
        } catch (IllegalGameMoveException e) {
            Assertions.assertNotNull(e, "Should fail to move pit 7");
        }
    }

    @Test
    public void testMovedPitItShouldBe0() throws IllegalGameMoveException, IllegalGameStateException{
        game.move(6);
        Assertions.assertEquals(0, game.getBoard().getPlayer1Pits()[5], "Pit moved should be empty");
        Assertions.assertEquals(1, game.getBoard().getPlayer1Mancala().getContent(), "Macala should have it's first stone");
        Assertions.assertEquals(7, game.getBoard().getPlayer2Pits()[0], "Opponents pit should have a stone");
    }

    @Test
    @AddConfig(key = "stonesQuantity", value = "5")
    public void testNextPlayerTurn() throws IllegalGameMoveException, IllegalGameStateException{
        game.move(1);
        Assertions.assertEquals(player1, game.getPlayerTurn(), "Player 1 should play again");
    }

    @Test
    @AddConfig(key = "stonesQuantity", value = "10")
    public void testSkipOpponentsMacala() throws IllegalGameMoveException, IllegalGameStateException{
        game.move(6);
        Assertions.assertEquals(0, game.getBoard().getPlayer1Pits()[5], "Pit 6 should be 0");
        Assertions.assertEquals(1, game.getBoard().getPlayer1Mancala().getContent(), "Player 1 Mancala should be 1");
        Assertions.assertEquals(11, game.getBoard().getPlayer2Pits()[5], "Player 2 pit should be 9");
        Assertions.assertEquals(0, game.getBoard().getPlayer2Mancala().getContent(), "Player 2 Mancala should be 0");
        Assertions.assertEquals(11, game.getBoard().getPlayer1Pits()[0], "Player 1 Pit should reive a stone");
        Assertions.assertEquals(11, game.getBoard().getPlayer1Pits()[1], "Player 1 Pit should reive a stone");
        Assertions.assertEquals(11, game.getBoard().getPlayer1Pits()[2], "Player 1 Pit should reive a stone");
    }





    
}
