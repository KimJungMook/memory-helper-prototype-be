package com.website.military.domain.dto.test.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    @Schema(description = "문제 번호 인덱스", example = "A")
    private String id;
    @Schema(description = "문제의 단어 뜻", example = "세트")
    private String meaning;
}
