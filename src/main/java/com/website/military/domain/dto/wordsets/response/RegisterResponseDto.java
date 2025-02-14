package com.website.military.domain.dto.wordsets.response;


import lombok.Data;

@Data

public class RegisterResponseDto {
    private Long setId;
    private String setName;

    public RegisterResponseDto(Long setId, String setName){
        this.setId = setId;
        this.setName = setName;
    }
}
