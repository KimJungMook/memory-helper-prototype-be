package com.website.military.domain.dto.auth.response;

import lombok.Data;

@Data
public class GetUserInfoFromUsernameResponseDto {
    private String username;
    private String email;

    public GetUserInfoFromUsernameResponseDto(String username, String email){
        this.username = username;
        this.email = email;
    }
}
