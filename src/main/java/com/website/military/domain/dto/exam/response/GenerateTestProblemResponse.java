package com.website.military.domain.dto.exam.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateTestProblemResponse {

    private Instant createdAt;
    private Long examId;
    private String examName;   
    private Long sourceWordsetId;
    private String sourceWordsetName;
    private List<GenerateProblemResponse> problems;

    
}
