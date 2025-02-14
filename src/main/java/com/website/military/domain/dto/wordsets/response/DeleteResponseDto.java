package com.website.military.domain.dto.wordsets.response;

import lombok.Data;

@Data

public class DeleteResponseDto {
    private Long id;
    private String setName;

    public DeleteResponseDto(Long id, String setName){
        this.id = id;
        this.setName = setName;
    }
}
