package com.website.military.domain.dto.word.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddWordToWordSetDto {
    private String word;
    private String meaning;
}
