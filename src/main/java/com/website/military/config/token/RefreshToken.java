package com.website.military.config.token;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {
    
    protected static final Map<String, Long> refreshTokens = new HashMap<>();

    public static Long getRefreshToken(final String refreshToken){
        return Optional.ofNullable(refreshTokens.get(refreshToken))
        .orElseThrow(() -> new RuntimeException("토큰 재발급이 안됩니다."));
    }

    public static void putRefreshToken(final String refreshToken, Long id){
        refreshTokens.put(refreshToken, id);
    }

    private static void removeRefreshToken(final String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

    public static void removeUserRefreshToken(final long refreshToken){
        // for(Map.Entry<String, Long> entry : refreshTokens.entrySet()){
        //     if(entry.getValue() == refreshToken){
        //         removeRefreshToken(entry.getKey());
        //     }
        // }
        refreshTokens.entrySet().removeIf(entry -> entry.getValue() == refreshToken);
    }
}
