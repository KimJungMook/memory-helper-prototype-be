package com.website.military.controller;

import com.website.military.config.websocket.LlmWebSocketHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/llm")
public class LlmController {

    public final LlmWebSocketHandler handler;

    public LlmController(LlmWebSocketHandler handler){
        this.handler = handler;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestLlm(@RequestBody Map<String, String> body) throws IOException{

        String prompt = body.get("prompt");

        String job = """
                {
                    "jobId" : "%s",
                    "prompt" : "%s"
                }
                """.formatted(UUID.randomUUID(), prompt);

        handler.sendJob(job);
        return ResponseEntity.ok("Job sent to LLM worker");
    }
}
