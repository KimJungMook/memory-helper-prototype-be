package com.website.military.domain.dto.results.response;

import java.util.List;

import com.website.military.domain.dto.exam.request.QuestionRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GradingResponse{
    private Long problemId;
    private Long problemNumber;
    private List<QuestionRequest> multipleChoice; // 문제에 해당하는 질문
    private String question;
    private int userAnswer;
    private int correctAnswer; // 정답
}
