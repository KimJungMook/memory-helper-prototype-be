package com.website.military.domain.dto.token.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {
    @Schema(description = "리프레시 토큰 입력", example = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNzQwMjAzODgyLCJleHAiOjE3NDAyMDU2ODJ9.siMwho-N2uBrl3Np0irsU_985i4zR5c4nHOrxCGkjWA")
    private String refreshToken;
}
