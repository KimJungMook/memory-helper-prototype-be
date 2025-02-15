package com.website.military.domain.dto.wordsets.response;

import java.time.Instant;
import java.util.List;


import lombok.Data;

@Data
public class GetWordsBySetIdResponse {
    private Long id;
    private String word;
    private List<String> noun;
    private List<String> verb;
    private List<String> adjective;
    private List<String> adverb;
    private Instant createdAt;
    private boolean isGpt;

    public GetWordsBySetIdResponse(Long id, String word, List<String> noun, List<String> verb,
    List<String> adjective, List<String> adverb, Instant createdAt, boolean isGpt){
        this.id = id;
        this.word = word;
        this.noun = noun;
        this.verb = verb;
        this.adjective = adjective;
        this.adverb = adverb;
        this.createdAt = createdAt;
        this.isGpt = isGpt;
    }
}
