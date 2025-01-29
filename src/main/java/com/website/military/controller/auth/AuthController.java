package com.website.military.controller.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.auth.request.LogInDto;
import com.website.military.domain.dto.auth.request.SignUpDto;
import com.website.military.domain.dto.auth.response.LoginResponseDto;
import com.website.military.domain.dto.auth.response.SignUpResponseDto;
import com.website.military.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    // GET


    // POST
    @Operation(summary = "signup", description = "회원가입")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = SignUpResponseDto.class))}),
        @ApiResponse(responseCode = "400", description = "해당 ID의 유저가 존재합니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> SignUp(@RequestBody SignUpDto dto) {
        ResponseEntity<?> entity = authService.signUp(dto);
        return entity;
    }

    @Operation(summary = "login", description = "로그인하기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = LoginResponseDto.class))}),
        @ApiResponse(responseCode = "400", description = "해당 ID의 유저가 존재합니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/login")
    public ResponseEntity<?> SignIn(@RequestBody LogInDto dto) {
        ResponseEntity<?> entity = authService.signIn(dto);
        return entity;
    }
    

    // PUT


    // DELETE


}
