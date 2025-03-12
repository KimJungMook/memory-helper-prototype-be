package com.website.military.domain.dto.test.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAllExamListResponse {
    private Long testId;
    private Instant createdAt;
    private int testType;
}
