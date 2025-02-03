package com.website.military.domain.dto.word.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddWordToWordSetResponseDto {
    private Long wordId;
    private String word;
    private List<String> noun;
    private List<String> verb;
    private List<String> adjective;
    private List<String> adverb;
}
