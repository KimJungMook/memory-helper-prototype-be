package com.website.military.domain.dto.wordsets.response;

import java.time.Instant;
import java.util.List;

import com.website.military.config.util.CommonUtils;
import com.website.military.domain.dto.response.WordClassResponse;

import lombok.Data;

@Data
public class GetWordsBySetIdResponse {
    private Long wordId;
    private String word;
    List<WordClassResponse> meaning;
    private Instant createdAt;
    private boolean isGpt;

    public GetWordsBySetIdResponse(Long wordId, String word, List<String> noun, List<String> verb,
    List<String> adjective, List<String> adverb, Instant createdAt, boolean isGpt){
        this.wordId = wordId;
        this.word = word;
        List<WordClassResponse> responseList = CommonUtils.meaningResponse(noun, verb, adjective, adverb);
        this.meaning = responseList;
        this.createdAt = createdAt;
        this.isGpt = isGpt;
    }
}
