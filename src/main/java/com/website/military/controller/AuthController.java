package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.auth.request.IdValidationDto;
import com.website.military.domain.dto.auth.request.LogInDto;
import com.website.military.domain.dto.auth.request.SignUpDto;
import com.website.military.domain.dto.auth.response.GetUserInfoFromUsernameResponseDto;
import com.website.military.domain.dto.auth.response.LoginResponseDto;
import com.website.military.domain.dto.auth.response.SignUpResponseDto;
import com.website.military.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    // GET
    @Operation(summary = "Get userInfo from token", description = "토큰을 통해서 아이디, 이름 알아낼 수 있게하는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = GetUserInfoFromUsernameResponseDto.class))}),
        @ApiResponse(responseCode = "400", description = "해당하는 정보가 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/user") // 토큰에 해당하는 유저의 정보
    public ResponseEntity<?> getUserInfoFromToken(HttpServletRequest request) {
        return authService.getUserInfoFromToken(request);
    }
    
    
    // POST
    @PostMapping("/check")
    public ResponseEntity<?> getMethodName(@RequestBody IdValidationDto dto) {
        return authService.idValidate(dto.getEmail());
    }
    
    @Operation(summary = "signup", description = "회원가입")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = SignUpResponseDto.class))}),
        @ApiResponse(responseCode = "400", description = "해당 ID의 유저가 존재합니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto dto) {
        return authService.signUp(dto);
    }

    @Operation(summary = "login", description = "로그인하기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = LoginResponseDto.class))}),
        @ApiResponse(responseCode = "400", description = "해당 ID의 유저가 존재합니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody LogInDto dto) {
        return authService.logIn(dto);
    }
    

    // PUT


    // DELETE
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest request){
        return authService.deleteUser(request);
  }
}
