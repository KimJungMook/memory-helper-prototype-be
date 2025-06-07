package com.website.military.domain.dto.test.response;

import java.util.List;

import com.website.military.domain.dto.test.request.QuestionRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckResponse {
    private Long problemId;
    private Long problemNumber;

    private List<QuestionRequest> multipleChoice; // 문제에 해당하는 질문

    private int userAnswer;
    private int correctAnswer; // 정답
}
