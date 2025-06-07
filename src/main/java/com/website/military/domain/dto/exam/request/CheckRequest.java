package com.website.military.domain.dto.exam.request;

import java.util.List;

import com.website.military.config.annotation.ValidAnswerChar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckRequest {

    @NotNull
    // private List<@ValidAnswerChar Character> checkedAnswers;
    @Schema(description = "문제 답 제출 하는 리스트", example = "[1, 2, 3, 4, 1, 2]")
    private List<Integer> checkedAnswers;

}
