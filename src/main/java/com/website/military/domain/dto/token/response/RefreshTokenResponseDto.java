package com.website.military.domain.dto.token.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenResponseDto{
    private String accessToken; 
    private String refreshToken;
}
