package com.website.military.domain.dto.auth.response;

import lombok.Data;

@Data
public class SignUpResponseDto {
    private String username;
    private String email;

    public SignUpResponseDto(String username, String email){
        this.username = username;
        this.email = email;
    }
}
