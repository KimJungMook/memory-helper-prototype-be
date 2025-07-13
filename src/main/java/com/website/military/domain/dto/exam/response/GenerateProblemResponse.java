package com.website.military.domain.dto.exam.response;

import java.util.List;

import com.website.military.domain.dto.exam.request.QuestionRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateProblemResponse {
    private Long problemId;
    private Long problemNumber;
    private List<QuestionRequest> multipleChoice;
    private String question;
    private int answer;
    
    public GenerateProblemResponse(Long problemNumber, List<QuestionRequest> multipleChoice, String question, int answer){
        this.problemNumber = problemNumber;
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.answer = answer;
    }
}
