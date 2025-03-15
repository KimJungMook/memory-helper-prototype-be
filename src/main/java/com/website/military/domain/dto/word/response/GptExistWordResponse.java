package com.website.military.domain.dto.word.response;

import java.util.List;

import lombok.Data;

@Data
public class GptExistWordResponse {
    private List<String> noun;
    private List<String> verb;
    private List<String> adjective;
    private List<String> adverb;

}
