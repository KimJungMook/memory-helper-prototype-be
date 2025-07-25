package com.website.military.domain.dto.exam.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAllExamListResponse {
    private Long examId;
    private String examName;
    private Instant createdAt;
    private Instant submittedAt;
    private int examType;  
    private int examProblemCount;
}
