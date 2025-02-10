package com.website.military.config.error;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.website.military.domain.dto.response.ResponseMessageDto;

import io.jsonwebtoken.MalformedJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize,  "잘못된 JWT 토큰입니다."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRunTimeJwtException(RuntimeException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize,  "잘못된 JWT 토큰입니다."));
    }
}
