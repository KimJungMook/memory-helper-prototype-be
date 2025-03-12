package com.website.military.domain.dto.test.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckResponse {

    private Long problemNumber;

    @Column(columnDefinition = "json")
    private String question; // 문제에 해당하는 질문

    @Column(columnDefinition = "CHAR(1)")
    private char answer; // 정답
}
