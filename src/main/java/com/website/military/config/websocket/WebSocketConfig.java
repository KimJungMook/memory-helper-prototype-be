package com.website.military.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

//    private final LlmWebSocketHandler llmWebSocketHandler;

    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfig(LlmWebSocketHandler llmWebSocketHandler, ChatWebSocketHandler chatWebSocketHandler){
//        this.llmWebSocketHandler = llmWebSocketHandler;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
//        registry.addHandler(llmWebSocketHandler, "/ws/llm")
//                .setAllowedOrigins("*");

        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*");
    }
}
