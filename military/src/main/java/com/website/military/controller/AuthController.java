package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.SignUpDto;
import com.website.military.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    // GET


    // POST
    @PostMapping("/signup")
    public ResponseEntity<?> SignUp(@RequestBody SignUpDto dto) {
        ResponseEntity<?> entity = authService.signUp(dto);
        return entity;
    }
    

    // PUT


    // DELETE


}
