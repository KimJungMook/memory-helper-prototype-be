package com.website.military.domain.dto.wordsets.response;


import java.time.Instant;
import lombok.Data;

@Data
public class WordSetsResponseDto {
    private Long setId;
    private String setName;
    private int problemSetsCount;
    private Instant createdAt;

    public WordSetsResponseDto(Long setId, String setName, Instant createdAt, int problemSetsCount){
        this.setId = setId;
        this.setName = setName;
        this.createdAt = createdAt;
        this.problemSetsCount = problemSetsCount;
    }
}
