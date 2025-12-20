package com.website.military.config.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.core.util.Json;
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

    private final LlmWebSocketHandler llmHandler;
    private final Map<String, WebSocketSession> jobSessionMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public ChatWebSocketHandler(LlmWebSocketHandler llmHandler){
        this.llmHandler = llmHandler;
        this.llmHandler.setChatHandler(this);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        if("LLM".equals(session.getHandshakeHeaders().getFirst("X-Client-Type"))){
//            llmsession = session;
//            System.out.println("Local LLM Connected");
//        }else{
        System.out.println("Browser Connected");
//        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = mapper.readTree(message.getPayload());

        if(!"USER_PROMPT".equals(json.get("type").asText())){
            return;
        }

        String jobId = UUID.randomUUID().toString();
        jobSessionMap.put(jobId, session);

        ObjectNode job = JsonNodeFactory.instance.objectNode();
        job.put("type", "LLM_JOB");
        job.put("jobId", jobId);
        job.put("prompt", json.get("message").asText());

        llmHandler.sendJob(job.toString());
    }

    public void handleLlmResult(String payload) throws Exception {
        JsonNode json = mapper.readTree(payload);

        String jobId = json.get("jobId").asText();
        WebSocketSession userSession = jobSessionMap.remove(jobId);

        if(userSession == null || !userSession.isOpen()) {
            return;
        }
        ObjectNode res = mapper.createObjectNode();
        res.put("type", "CHAT_MESSAGE");
        res.put("role", "assistant");
        res.put("message", json.get("result").asText());

        userSession.sendMessage(new TextMessage(res.toString()));
    }

}
