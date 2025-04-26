package com.website.military.domain.dto.word.response;

import java.util.List;

import com.website.military.config.util.CommonUtils;
import com.website.military.domain.dto.response.WordClassResponse;

import lombok.Data;

@Data
public class UpdateMeaningResponseDto {
    private Long wordId;
    List<WordClassResponse> meaning;
    
    public UpdateMeaningResponseDto(Long wordId, List<String> noun, List<String> verb, 
    List<String> adjective, List<String> adverb){
        List<WordClassResponse> responseList = CommonUtils.meaningResponse(noun, verb, adjective, adverb);
        this.wordId = wordId;
        this.meaning = responseList;
    }
}
