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
    private String question;
    private List<QuestionRequest> multipleChoice;
    private QuestionRequest userAnswers;

    private QuestionRequest rightAnswers;

    public GenerateProblemResponse(Long problemNumber, String question, List<QuestionRequest> multipleChoice, QuestionRequest rightAnswers){
        this.problemNumber = problemNumber;
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.rightAnswers = rightAnswers;
    }
}
