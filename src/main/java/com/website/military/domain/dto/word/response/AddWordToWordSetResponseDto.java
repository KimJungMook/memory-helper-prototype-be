package com.website.military.domain.dto.word.response;

import java.util.List;

import com.website.military.config.util.CommonUtils;
import com.website.military.domain.dto.response.WordClassResponse;

import lombok.Data;

@Data
public class AddWordToWordSetResponseDto {
    private Long wordId;
    private String word;
    private List<WordClassResponse> meaning;
    public AddWordToWordSetResponseDto(Long wordId, String word, List<String> noun, List<String> verb, 
    List<String> adjective, List<String> adverb){
        this.wordId = wordId;
        this.word = word;
        List<WordClassResponse> responseList = CommonUtils.meaningResponse(noun, verb, adjective, adverb);
        this.meaning = responseList;
    }
}
