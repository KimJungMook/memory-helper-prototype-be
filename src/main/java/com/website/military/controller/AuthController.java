package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.config.token.RefreshTokenService;
import com.website.military.domain.dto.auth.request.IdValidationDto;
import com.website.military.domain.dto.auth.request.LogInDto;
import com.website.military.domain.dto.auth.request.SignUpDto;
import com.website.military.domain.dto.auth.response.DeleteUserResponse;
import com.website.military.domain.dto.auth.response.GetUserInfoFromUsernameResponseDto;
import com.website.military.domain.dto.auth.response.LoginResponseDto;
import com.website.military.domain.dto.auth.response.SignUpResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.token.request.RefreshTokenDto;
import com.website.military.domain.dto.token.response.RefreshTokenResponseDto;
import com.website.military.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    // GET
    @Operation(summary = "토큰을 통해 회원정보 얻기", description = "토큰을 통해서 아이디, 이름 알아낼 수 있게하는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = GetUserInfoFromUsernameResponseDto.class),
                examples = @ExampleObject(value = "{\"code\": \"OK\", \"data\": { \"username\": \"묵\", \"email\": \"wjdanr@naver.com\" } }"
                ))}),
        @ApiResponse(responseCode = "400", description = "해당하는 정보가 없습니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                    examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"해당하는 정보가 없습니다.\" } }"
                    ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                    examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                    ))})
    })
    @GetMapping("/user") // 토큰에 해당하는 유저의 정보
    public ResponseEntity<?> getUserInfoFromToken(HttpServletRequest request) {
        return authService.getUserInfoFromToken(request);
    }
    
    
    // POST
    @Operation(summary = "아이디 체크 ", description = "아이디 중복확인 하는데에 사용하는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "{ \"code\": \"OK\", \"data\": { \"message\": \"사용가능한 아이디입니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "400", description = "존재하는 아이디가 있습니다.", 
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"존재하는 아이디가 있습니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러", 
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/check")
    public ResponseEntity<?> getMethodName(@RequestBody IdValidationDto dto) {
        return authService.idValidate(dto.getEmail());
    }
    
    @Operation(summary = "회원가입", description = "회원가입 하는데에 사용하는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = SignUpResponseDto.class),
                examples = @ExampleObject(value = "{\"code\": \"OK\", \"data\": { \"username\": \"묵\", \"email\": \"wjdanr@naver.com\" } }"
                ))}),
        @ApiResponse(responseCode = "400", description = "해당 ID의 유저가 존재합니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"해당 ID의 유저가 존재합니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto dto) {
        return authService.signUp(dto);
    }

    @Operation(summary = "로그인 하기", description = "로그인하는데 필요한 메서드, 토큰 반환")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = LoginResponseDto.class),
                examples = @ExampleObject(value = "{\"code\": \"OK\", \"data\": { \"username\": \"묵\", \"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNzQxMDExODI0LCJleHAiOjE3NDEwMTM2MjR9.HNuSK7iiYKQ_W7x6jUxAPcwnHolTodPpQfXtND-H0lI\", \"refreshToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNzQxMDExODI0LCJleHAiOjE3NDEwMTM2MjR9.HNuSK7iiYKQ_W7x6jUxAPcwnHolTodPpQfXtND-H0lI\"} }"
                ))}),
        @ApiResponse(responseCode = "400", description = "아이디와 비밀번호가 일치하지않습니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"아이디와 비밀번호가 일치하지 않습니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "400", description = "아이디가 존재하지 않습니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"아이디가 존재하지 않습니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody LogInDto dto) {
        return authService.logIn(dto);
    }
    
    @Operation(summary = "토큰 재발급 하기", description = "리프레쉬 토큰을 이용해서 토큰 재발급")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = RefreshTokenResponseDto.class),
                examples = @ExampleObject(value = "{\"code\": \"OK\", \"data\": { \"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNzQxMDExODI0LCJleHAiOjE3NDEwMTM2MjR9.HNuSK7iiYKQ_W7x6jUxAPcwnHolTodPpQfXtND-H0lI\", \"refreshToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNzQxMDExODI0LCJleHAiOjE3NDEwMTM2MjR9.HNuSK7iiYKQ_W7x6jUxAPcwnHolTodPpQfXtND-H0lI\"} }"
                ))}),
        @ApiResponse(responseCode = "401", description = "잘못된 JWT 토큰입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 JWT 토큰입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/token-refresh")
    public ResponseEntity<?> tokenRefresh(@RequestBody RefreshTokenDto dto){
        RefreshTokenResponseDto response = refreshTokenService.refreshToken(dto.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return authService.logout(request);
    }
    
    // PUT


    // DELETE
    @Operation(summary = "회원탈퇴하기 ", description = "회원 탈퇴하는데 사용하는 api, 로그인 한 상태에서만 가능.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = DeleteUserResponse.class),
                examples = @ExampleObject(value = "{\"code\": \"OK\", \"data\": { \"username\": \"묵\", \"email\": \"wjdanr@naver.com\" } }"
                ))}),
        @ApiResponse(responseCode = "400", description = "존재하지않는 유저입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"존재하지 않는 유저입니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest request){
        return authService.deleteUser(request);
    }

}


// auth만 예시 들어감 나머지 Controller도 하기.