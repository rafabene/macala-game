package com.rafabene.mancala.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.rafabene.mancala.domain.Game;
import com.rafabene.mancala.domain.GameStatus;
import com.rafabene.mancala.domain.IllegalGameStateException;
import com.rafabene.mancala.domain.Player;
import com.rafabene.mancala.web.input.InputDecoder;
import com.rafabene.mancala.web.input.WebsocketInput;
import com.rafabene.mancala.web.output.OutputEncoder;
import com.rafabene.mancala.web.output.WebsocketOutput;

@ServerEndpoint(value = "/game", encoders = OutputEncoder.class, decoders = { InputDecoder.class })
@ApplicationScoped
public class MacalaWebsocketServer {

    private Logger logger = Logger.getLogger(this.getClass().toString());
    
    @Inject
    private Game game;

    private static Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void open(Session session) {
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

    @OnClose
    public void close(Session session) {
        sessions.remove(session);
        String sessionId = session.getId();
        logger.info("Session diconnected: " + sessionId);
        boolean wasPlaying = game.removePlayer(new Player(sessionId));
        String message = wasPlaying ? String.format("Player %s disconnected. Game Stopped!", sessionId) : "";
        // Notify all sesssions only if the the player was playing
        notifySessions(message);
    }

    @OnError
    public void onError(Throwable error) {
        logger.severe(error.getMessage());
        error.printStackTrace();
    }

    @OnMessage
    public void handleMessage(WebsocketInput input, Session session) {
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
        for (Session session : sessions) {
            notifySession(session, message);
        }
    }

    private void notifySession(Session session, String message) {
        try {
            session.getBasicRemote().sendObject(new WebsocketOutput(session.getId(), game, message));
        } catch (IOException | EncodeException e) {
            logger.severe(e.getMessage());
        }
    }

}
