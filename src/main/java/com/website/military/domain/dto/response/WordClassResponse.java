package com.website.military.domain.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class WordClassResponse {
    private String type;
    private List<String> meaning;
}
