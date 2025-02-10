package com.website.military.config.token;

import org.springframework.stereotype.Service;

import com.website.military.config.jwt.JwtProvider;
import com.website.military.domain.dto.token.response.RefreshTokenResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final JwtProvider jwtProvider;

    public RefreshTokenResponseDto refreshToken(final String refreshToken){
        checkRefreshToken(refreshToken);
        var id = RefreshToken.getRefreshToken(refreshToken);
        String newAccessToken = jwtProvider.generateAccessToken(id);

        RefreshToken.removeUserRefreshToken(id);
        String newRefreshToken = jwtProvider.generateRefreshToken(id);
        RefreshToken.putRefreshToken(newRefreshToken, id);
        return RefreshTokenResponseDto.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
    }

    private void checkRefreshToken(final String refreshToken){
        if(Boolean.FALSE.equals(jwtProvider.validateToken(refreshToken)))
            throw new RuntimeException("토큰이 만료됐습니다.");
    }
}
