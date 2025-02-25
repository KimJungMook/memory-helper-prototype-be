package com.website.military.domain.dto.wordsets.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordSetsDto {
    @Schema(description = "세트 이름 입력", example = "세트1")
    private String setName;
}
