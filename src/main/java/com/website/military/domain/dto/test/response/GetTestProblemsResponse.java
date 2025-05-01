package com.website.military.domain.dto.test.response;

import java.util.List;

import com.website.military.domain.dto.test.request.QuestionRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetTestProblemsResponse {
    private Long problemId;
    
    private Long problemNumber;

    private String question;
    
    private List<QuestionRequest> multipleChoice; // 문제에 해당하는 질문
    
    private char answer; // 정답
}
