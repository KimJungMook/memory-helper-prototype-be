package com.website.military.domain.dto.exam.response;

import java.util.List;

import com.website.military.domain.dto.exam.request.QuestionRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateExamListResponse {
    private Long problemNumber;
    private List<QuestionRequest> multipleChoice;
    private String question;
    private int answer;
}
