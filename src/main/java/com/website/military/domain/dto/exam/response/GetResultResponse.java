package com.website.military.domain.dto.exam.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetResultResponse {
    
    private Long resultId;
    private Instant createaAt;
    private int totalProblemSum;

}


