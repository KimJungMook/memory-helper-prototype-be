package com.website.military.domain.dto.wordsets.response;


import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordSetsResponseDto {
    private Long setId;
    private String setName;
    private Instant createdAt;
}
