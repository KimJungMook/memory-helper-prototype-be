package com.website.military.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LlmWebSocketHandler llmWebSocketHandler;

    public WebSocketConfig(LlmWebSocketHandler llmWebSocketHandler){
        this.llmWebSocketHandler = llmWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(llmWebSocketHandler, "/ws/llm")
                .setAllowedOrigins("*");
    }
}
