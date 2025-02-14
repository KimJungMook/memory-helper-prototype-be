package com.website.military.domain.dto.auth.response;

import lombok.Data;

@Data
public class DeleteUserResponse {
    private String username;
    private String email;

    public DeleteUserResponse(String username, String email){
        this.username = username;
        this.email = email;
    }
}
