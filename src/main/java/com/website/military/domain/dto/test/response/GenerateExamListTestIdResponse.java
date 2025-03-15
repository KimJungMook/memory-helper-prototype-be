package com.website.military.domain.dto.test.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateExamListTestIdResponse {
    private Long testId;
    private List<GenerateExamListResponseDto> examList;
}
