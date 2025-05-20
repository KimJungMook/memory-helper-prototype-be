package com.website.military.domain.dto.test.request;

import java.util.List;

import com.website.military.config.annotation.ValidAnswerChar;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckRequest {

    @NotNull
    private List<@ValidAnswerChar Character> checkedAnswers;
}
