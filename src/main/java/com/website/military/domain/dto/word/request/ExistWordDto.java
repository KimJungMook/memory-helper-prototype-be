package com.website.military.domain.dto.word.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistWordDto {
    @Schema(description = "단어 입력", example = "word")
    private String word;
}
