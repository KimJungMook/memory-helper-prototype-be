package com.website.military.config.error;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.website.military.domain.dto.response.ResponseMessageDto;

import io.jsonwebtoken.MalformedJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        AtomicReference<String> errorMessage = new AtomicReference<>("");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.set(error.getDefaultMessage());
            errors.put(error.getField(), error.getDefaultMessage());
        });
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, errorMessage.get())); 이게 메세지를 나오게 하는 방법
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError,  "잘못된 요청입니다."));
    }
    
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize,  "잘못된 JWT 토큰입니다."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRunTimeJwtException(RuntimeException e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError,  "잘못된 요청입니다."));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", ex.getStatus().value());
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }
}
