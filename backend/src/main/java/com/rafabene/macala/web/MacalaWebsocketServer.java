package com.rafabene.macala.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.rafabene.macala.domain.Game;
import com.rafabene.macala.domain.IllegalGameStageException;
import com.rafabene.macala.domain.Player;
import com.rafabene.macala.web.input.InputDecoder;
import com.rafabene.macala.web.input.WebsocketInput;
import com.rafabene.macala.web.output.OutputEncoder;
import com.rafabene.macala.web.output.WebsocketOutput;

@ServerEndpoint(value = "/game", encoders = OutputEncoder.class, decoders = { InputDecoder.class })
public class MacalaWebsocketServer {

    private Logger logger = Logger.getLogger(this.getClass().toString());
    private Game game = Game.getInstance();
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
        } catch (IllegalGameStageException e) {
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
        String message = wasPlaying ? String.format("Player %s disconnected", sessionId) : "";
        // Notify all sesssions
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
            switch (input.getOperation()){
                case START_GAME: 
                    game.startGame();
                    notifySessions("Game Started");
                    break;
                default:
            }                
        } catch (IllegalGameStageException e) {
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
