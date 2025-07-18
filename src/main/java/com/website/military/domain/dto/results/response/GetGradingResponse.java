package com.website.military.domain.dto.results.response;

import java.time.Instant;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetGradingResponse {
    private Long resultId;
    private List<GradingResponse> correctList;
    private List<GradingResponse> incorrectList;
    private Instant submittedAt;
}
