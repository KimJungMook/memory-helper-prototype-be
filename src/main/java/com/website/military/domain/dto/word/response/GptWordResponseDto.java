package com.website.military.domain.dto.word.response;


import java.util.List;

import lombok.Data;

@Data
public class GptWordResponseDto {
    List<String> noun;
    List<String> verb;
    List<String> adjective;
    List<String> adverb;
    boolean isGpt;

    public GptWordResponseDto(List<String> noun,List<String> verb,List<String> adjective,List<String> adverb, boolean isGpt){
        this.noun = noun;
        this.verb = verb;
        this.adjective = adjective;
        this.adverb = adverb;
        this.isGpt = isGpt;
        
    }
}
