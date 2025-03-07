package com.website.military.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.website.military.config.jwt.JwtProvider;
import com.website.military.config.redis.RedisUtil;
import com.website.military.config.token.RefreshToken;
import com.website.military.domain.Entity.User;
import com.website.military.domain.dto.auth.request.LogInDto;
import com.website.military.domain.dto.auth.request.SignUpDto;
import com.website.military.domain.dto.auth.response.DeleteUserResponse;
import com.website.military.domain.dto.auth.response.GetUserInfoFromUsernameResponseDto;
import com.website.military.domain.dto.auth.response.LoginResponseDto;
import com.website.military.domain.dto.auth.response.SignUpResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final RedisUtil redisUtil;
    
    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;
    // 아이디 있는지 체크하는데 사용하는 메서드
    public ResponseEntity<?> idValidate(String email){
        Optional<User> existingUser = userRepository.findByEmail(email);
        if(existingUser.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseMessageDto.set(badRequestError, "존재하는 아이디가 있습니다."));
        }
        return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseMessageDto.set("OK", "사용가능한 아이디입니다."));
    }

    // 회원가입 하는데 사용하는 메서드
    public ResponseEntity<?> signUp(SignUpDto dto){
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if(existingUser.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }
        
        try{
            String password = passwordEncoder.encode(dto.getPassword());
            User user = new User(dto.getUsername(), dto.getEmail(), password);
            user.setCreatedAt(Instant.now());
            userRepository.save(user);
            SignUpResponseDto response = new SignUpResponseDto(user.getEmail(), user.getUsername());
        return ResponseEntity.ok(ResponseDataDto.set("OK", response));
        }catch(Exception e){
            e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set(internalError, "서버 에러"));
        }
    }

    // 로그인하는데 사용하는 메서드 -> 아직 로그아웃을 하고 재 로그인을 했을 때, 이 전 껄로 했을 때, 인증이 되고 있음. 
    public ResponseEntity<?> logIn(LogInDto dto){
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if(existingUser.isPresent()){
            User user = existingUser.get();
            if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
                Long userId = user.getUserId();
                String username = user.getUsername();
                String accessToken = jwtProvider.generateAccessToken(userId);
                RefreshToken.removeUserRefreshToken(userId);
                String refreshToken = jwtProvider.generateRefreshToken(userId);
                RefreshToken.putRefreshToken(refreshToken, userId);
                LoginResponseDto response = new LoginResponseDto(username, accessToken, refreshToken);
                return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDataDto.set("OK", response));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

    public ResponseEntity<?> logout(HttpServletRequest request){
        final String token = request.getHeader("Authorization");
        if(token == null || !token.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }
        String jwtToken = token.substring(7);

        if(!jwtProvider.validateToken(jwtToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "이미 로그아웃"));
        }
        redisUtil.setBlackList(jwtToken, "accessToken", 5);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseMessageDto.set("logout", "로그아웃 완료"));
    }

    public ResponseEntity<?> deleteUser(HttpServletRequest request){
        Long loginUserId = getUserId(request);
        Optional<User> existingUser = userRepository.findById(loginUserId);
        if(existingUser.isPresent()){
                User user = existingUser.get();
                DeleteUserResponse response = new DeleteUserResponse(user.getEmail(), user.getUsername());
                userRepository.deleteById(loginUserId);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }


    // 이름을 통해서 사용자의 정보를 알아내는 메서드 
    public ResponseEntity<?> getUserInfoFromToken(HttpServletRequest request){
        Long userId = getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            GetUserInfoFromUsernameResponseDto response = new GetUserInfoFromUsernameResponseDto(user.getEmail(), user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

   // 인증관련 유저의 id 알아내는 메서드
    public Long getUserId(HttpServletRequest request){
        final String token = request.getHeader("Authorization");
        String id = null;
        if(token != null && !token.isEmpty()){
            String jwtToken = token.substring(7);
            id = jwtProvider.getUserIdFromToken(jwtToken);
        }
        return Long.parseLong(id);
    }

}

// refreshToken으로 해도 로그인을 한 것과 같은 효과가 나고 있음.