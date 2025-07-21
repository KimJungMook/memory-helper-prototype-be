package com.website.military.domain.dto.exam.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeExamName {
    @Schema(description = "시험이름 입력", example = "임어진은 2월 전역")
    private String testName;
}
