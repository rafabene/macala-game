package com.rafabene.mancalagame.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.bind.Jsonb;

import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.GameConfiguration;
import com.rafabene.mancala.domain.GameStatus;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Player;
import com.rafabene.mancalagame.web.input.WebsocketInput;
import com.rafabene.mancalagame.web.output.WebsocketOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MancalaWebsockeHandler extends TextWebSocketHandler {

    private Logger logger = Logger.getLogger(this.getClass().toString());
    
    private Game game;

    private static Set<WebSocketSession> sessions = new HashSet<>();

    @Autowired
    private Jsonb jsonb;

    @Value("${stonesQuantity}")
    private int stonesQuantiy;

    @Value("${pitsQuantity}")
    private int pitsQuantity;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (game == null){
            game = new Game(new GameConfiguration(pitsQuantity, stonesQuantiy));
        }
        sessions.add(session);
        logger.info("Session connected: " + session.getId());
        String sessionId = session.getId();
        String message = null;
        Player newPlayer = new Player(sessionId);
        try {
            game.addNewPlayer(newPlayer);
            message = "Welcome player " + newPlayer.getGamerId();
            logger.info("New player connected: " + newPlayer);
            // Message for all sessions
            notifySessions(message);
        } catch (IllegalGameStateException e) {
            message = e.getMessage();
            // Message only for this session
            notifySession(session, message);
        }
    }

    @Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
                WebsocketInput input = jsonb.fromJson(message.getPayload(), WebsocketInput.class);
                logger.info("Received input: " + input + " from session " + session.getId());
                try {
                    switch (input.getOperation()) {
                        case START_GAME:
                            game.startGame();
                            notifySessions("Game Started");
                            break;
                        case SEED:
                            game.move(Integer.valueOf(input.getParameter()));
                            notifySessions(String.format("Player %s has moved.<br/>It's now player %s turn", session.getId(),
                                    game.getPlayerTurn().getGamerId()));
                            break;
                        case RESET_GAME:
                            game.getBoard().reset();
                            notifySessions("Game restarted by " + session.getId());
                            break;
                    }
                    if (game.getGameStatus().equals(GameStatus.GAME_OVER)) {
                        notifySessions(String.format("GAME OVER. Player %s won!!!", game.getWinner().getGamerId()));
                    }
                } catch (Exception e) {
                    notifySession(session, e.getMessage());
                }
	}
    

    private void notifySessions(String message) {
        for (WebSocketSession session : sessions) {
            notifySession(session, message);
        }
    }

    private void notifySession(WebSocketSession session, String message) {
        try {
            String json = jsonb.toJson(new WebsocketOutput(session.getId(), game, message));
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }
}
