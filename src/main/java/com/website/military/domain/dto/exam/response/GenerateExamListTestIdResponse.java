package com.website.military.domain.dto.exam.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateExamListTestIdResponse {
    private Long testId;
    private List<GenerateExamListResponse> examList;
}
