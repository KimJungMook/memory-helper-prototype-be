package com.website.military.domain.dto.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String username;
    private String accessToken;
    private String refreshToken;
}
