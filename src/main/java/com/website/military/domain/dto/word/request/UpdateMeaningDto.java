package com.website.military.domain.dto.word.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMeaningDto {
    private String word;
    private List<String> noun;
    private List<String> verb;
    private List<String> adjective;
    private List<String> adverb;
}
