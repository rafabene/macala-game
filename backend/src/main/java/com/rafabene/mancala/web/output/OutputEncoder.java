package com.rafabene.mancala.web.output;

import java.util.logging.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class OutputEncoder implements Encoder.Text<WebsocketOutput> {

    private Jsonb jsonb;
    private Logger logger = Logger.getLogger(this.getClass().toString());

    @Override
    public void init(EndpointConfig config) {
        JsonbConfig jsonbConfig = new JsonbConfig()
                // Formating will help the frontend designer
                .withFormatting(true);

        // Create Jsonb with custom configuration
        jsonb = JsonbBuilder.create(jsonbConfig);
    }

    @Override
    public void destroy() {

    }

    @Override
    public String encode(WebsocketOutput websocketProtocol) throws EncodeException {
        try {
            String result = jsonb.toJson(websocketProtocol);
            return result;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return null;
        }
    }

}
