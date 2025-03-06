package com.website.military.domain.dto.wordsets.response;

import lombok.Data;

@Data
public class DeleteResponseDto {
    private Long setId;
    private String setName;

    public DeleteResponseDto(Long setId, String setName){
        this.setId = setId;
        this.setName = setName;
    }
}
