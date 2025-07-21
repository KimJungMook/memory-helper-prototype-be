package com.website.military.domain.dto.exam.response;

import java.util.List;

import com.website.military.domain.dto.exam.request.QuestionRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProblemResponse {
    private Long problemId;
    private Long problemNumber;
    private String question;
    private List<QuestionRequest> multipleChoice; // 문제에 해당하는 질문
}
