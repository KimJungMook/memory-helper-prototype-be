package com.website.military.config.jwt;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private static final long JWT_TOKEN_VALID = (long) 1000*60*30;
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String getUsernameFromToken(final String token){
        return getClaimFromToken(token, Claims::getId);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver){
        if(Boolean.FALSE.equals(validateToken(token)))
            return null;

            final Claims claims = getAllClaimsFromToken(token);

            return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(final String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpireationDateFromToken(final String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String generateAccessToken(final String id){
        return generateAccessToken(id, new HashMap<>());
    }

    public String generateAccessToken(final long id){
        return generateAccessToken(String.valueOf(id), new HashMap<>());
    }

    public String generateAccessToken(final String id, final Map<String, Object> claims){
        return doGenerateAccessToken(id, claims);
    }

    private String doGenerateAccessToken(final String id, final Map<String, Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+JWT_TOKEN_VALID))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(final String id){
        return doGenerateRefreshToken(id);
    }

    public String generateRefreshToken(final long id){
        return doGenerateRefreshToken(String.valueOf(id));
    }

    private String doGenerateRefreshToken(final String id){
        return Jwts.builder()
                .setId(id)
                .setExpiration(new Date(System.currentTimeMillis()+JWT_TOKEN_VALID))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(final String token){
        try{
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        }catch (SecurityException e){
            log.warn("Invalid JWT signature: {}", e.getMessage());
        }catch (MalformedJwtException e){
            log.warn("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            log.warn("JWT token is expired: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            log.warn("JWT token is unsupported: {}", e.getMessage());    
        }catch (IllegalArgumentException e){
            log.warn("JWT claims string is empty: {}", e.getMessage());    
        }

        return false;
    }

}
