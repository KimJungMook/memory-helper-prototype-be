package com.website.military.domain.dto.test.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateExamListResponseDto {
    private Long problemNumber;
    private List<GenerateExamListResponse> multipleChoice;
    private String question;
    private int answer;
}
