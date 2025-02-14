package com.website.military.domain.dto.word.response;

import java.util.List;

import lombok.Data;

@Data
public class GptWordResponseDto {
    List<List<String>> meanings;
    boolean isGpt;

    public GptWordResponseDto(List<List<String>> meanings, boolean isGpt){
        this.meanings = meanings;
        this.isGpt = isGpt;
        
    }
}
