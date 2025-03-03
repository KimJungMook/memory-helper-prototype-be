package com.website.military.domain.dto.test.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteExamResponse {
    private Long testId;
    private String setName;
    private Instant deletedAt;
}
