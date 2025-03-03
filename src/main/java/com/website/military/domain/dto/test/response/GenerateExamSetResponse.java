package com.website.military.domain.dto.test.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateExamSetResponse {
    private Long setId;
    private Long examId;
    private int testType;
    private Instant createdAt;
}   
