package com.website.military.domain.dto.exam.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetExamIdResponse {

    private Instant createdAt;
    private Long examId;
    private String examName; 
    private Long sourceWordSetId;
    private String sourceWordSetName; 
    private List<ProblemResponse> problemResponses;
    private List<ResultResponse> resultResponses;

    public GetExamIdResponse(Instant createdAt, Long examId, String examName, Long sourceWordSetId, String sourceWordSetName, List<ProblemResponse> problemResponses){
        this.createdAt = createdAt;
        this.examId = examId;
        this.examName = examName;
        this.sourceWordSetId = sourceWordSetId;
        this.sourceWordSetName = sourceWordSetName;
        this.problemResponses = problemResponses;
    }
}
