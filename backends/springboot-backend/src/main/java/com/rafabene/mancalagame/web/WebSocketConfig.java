package com.rafabene.mancalagame.web;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getGameHandler(), "/game").setAllowedOrigins("*");
    } 

    @Bean
    public WebSocketHandler getGameHandler(){
        return new MancalaWebsockeHandler();
    }
    
    @Bean
    public Jsonb getConfigJsonb(){
        JsonbConfig jsonbConfig = new JsonbConfig()
        // Formating will help the frontend designer
        .withFormatting(true);

        // Create Jsonb with custom configuration
        Jsonb jsonb = JsonbBuilder.create(jsonbConfig);
        return jsonb;
    }

}
