package com.website.military.domain.dto.word.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistWordDto {
    @Schema(description = "단어 입력", example = "word")
    @NotNull
    @Size(min = 1, max = 30, message = "단어는 1자이상 30자이하여야합니다.")
    private String word;
}
