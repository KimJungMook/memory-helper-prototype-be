package com.website.military.domain.dto.results.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetGradingResponse {
    
    private Long resultId;
    private double score;
    private Instant submmitedAt;
}
