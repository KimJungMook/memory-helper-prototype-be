package com.website.military.domain.dto.exam.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultResponse {
    private Long resultId;
    private Instant createaAt;
    private Long totalProblemsNum;
    private Long correctedAnswersNum;
}
