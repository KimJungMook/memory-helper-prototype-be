package com.website.military.domain.dto.test.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateExamListResponseDto {
    private Long problemNumber;
    private List<GenerateExamListResponse> list;
    private String question;
    private String answer;
}
