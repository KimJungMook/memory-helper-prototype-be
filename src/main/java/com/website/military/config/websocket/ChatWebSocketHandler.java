package com.website.military.config.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private WebSocketSession llmsession;
    private final Map<String, WebSocketSession> jobSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if("LLM".equals(session.getHandshakeHeaders().getFirst("X-Client-Type"))){
            llmsession = session;
            System.out.println("Local LLM Connected");
        }else{
            System.out.println("Browser Connected");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = new ObjectMapper().readTree(message.getPayload());

        if("USER_PROMPT".equals(json.get("type").asText())){
            String jobId = UUID.randomUUID().toString();

            jobSessionMap.put(jobId, session);

            ObjectNode job = JsonNodeFactory.instance.objectNode();
            job.put("type", "LLM_JOB");
            job.put("jobId", jobId);
            job.put("prompt", json.get("message").asText());

            llmsession.sendMessage(new TextMessage(job.toString()));
        }

        if("LLM_RESULT".equals(json.get("type").asText())){
            String jobId = UUID.randomUUID().toString();
            WebSocketSession userSession = jobSessionMap.remove(jobId);

            ObjectNode res = JsonNodeFactory.instance.objectNode();

            res.put("type", "CHAT_MESSAGE");
            res.put("role", "assistant");
            res.put("messsage", json.get("result").asText());

            userSession.sendMessage(new TextMessage(res.toString()));
        }
    }
}
