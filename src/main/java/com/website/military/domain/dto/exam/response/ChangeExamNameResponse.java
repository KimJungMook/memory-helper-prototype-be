package com.website.military.domain.dto.exam.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeExamNameResponse {
    
    private Long examId;
    private String examName;
}
