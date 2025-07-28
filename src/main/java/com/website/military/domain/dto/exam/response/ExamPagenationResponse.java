package com.website.military.domain.dto.exam.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamPagenationResponse {
    private Long examId;
    private String examName;
    private Instant GeneratedAt;
    private Long TimesStdied;
    private Instant RecentStudiedAt;
    private Long ProblemCount;
}
