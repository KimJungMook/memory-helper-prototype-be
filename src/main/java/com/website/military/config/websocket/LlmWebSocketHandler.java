package com.website.military.config.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LlmWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){
        System.out.println("Result from worker: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        sessions.remove(session);
    }

    public  void sendJob(String jobJson) throws IOException{
        for(WebSocketSession session : sessions){
            if(session.isOpen()){
                session.sendMessage(new TextMessage(jobJson));
            }
        }
    }
}
