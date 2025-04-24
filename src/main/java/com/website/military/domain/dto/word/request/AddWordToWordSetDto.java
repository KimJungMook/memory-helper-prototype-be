package com.website.military.domain.dto.word.request;

import java.util.List;

import com.website.military.domain.dto.response.WordClassResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddWordToWordSetDto {
    @Schema(description = "단어 입력", example = "word")
    private String word;
    @Schema(description = "명사 뜻 입력", example = "[\"단어\"]")
    private List<String> noun;
    @Schema(description = "동사 뜻 입력", example = "[\"단어를 입력하다.\"]")
    private List<String> verb;
    @Schema(description = "형용사 뜻 입력", example = "[]")
    private List<String> adjective;
    @Schema(description = "부사 뜻 입력", example = "[]")
    private List<String> adverb;
    // private List<WordClassResponse> meaning;
}
