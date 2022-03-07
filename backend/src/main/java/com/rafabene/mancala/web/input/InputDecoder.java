package com.rafabene.mancala.web.input;

import java.util.logging.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class InputDecoder implements Decoder.Text<WebsocketInput> {

    private Jsonb jsonb = JsonbBuilder.create();

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName().toString());

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public WebsocketInput decode(String s) throws DecodeException {
        WebsocketInput output = jsonb.fromJson(s, WebsocketInput.class);
        return output;
    }

    @Override
    public boolean willDecode(String s) {
        try {
            decode(s);
            return true;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return false;
        }

    }

}
