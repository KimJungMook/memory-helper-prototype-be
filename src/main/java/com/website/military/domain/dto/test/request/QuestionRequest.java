package com.website.military.domain.dto.test.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private String id;
    private String meaning;
}
