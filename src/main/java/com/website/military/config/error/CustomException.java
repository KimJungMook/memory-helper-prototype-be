package com.website.military.config.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    
    private final HttpStatus status;

    public CustomException(HttpStatus httpStatus, String message){
        super(message);
        this.status = httpStatus;
    }
}
