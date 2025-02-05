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
import com.website.military.config.token.RefreshToken;
import com.website.military.domain.Entity.User;
import com.website.military.domain.dto.auth.request.LogInDto;
import com.website.military.domain.dto.auth.request.SignUpDto;
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

    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;
 
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
            .body(ResponseMessageDto.set(badRequestError, "존재하는 아이디가 있습니다."));
        }
        
        try{
            String password = passwordEncoder.encode(dto.getPassword());
            User user = new User(dto.getUsername(), dto.getEmail(), password);
            user.setCreatedAt(Instant.now());
            userRepository.save(user);
            SignUpResponseDto responseDto = SignUpResponseDto.builder()
                                            .email(user.getEmail())
                                            .username(user.getUsername()).build();
        return ResponseEntity.ok(ResponseDataDto.set("OK", responseDto));
        }catch(Exception e){
            e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set(internalError, "서버 에러"));
        }
    }

    // 로그인하는데 사용하는 메서드
    public ResponseEntity<?> logIn(LogInDto dto){
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if(existingUser.isPresent()){
            if(passwordEncoder.matches(dto.getPassword(), existingUser.get().getPassword())){
                Long id = existingUser.get().getUserId();
                String username = existingUser.get().getUsername();
                String accessToken = jwtProvider.generateAccessToken(id);
                RefreshToken.removeUserRefreshToken(id);
                String refreshToken = jwtProvider.generateRefreshToken(id);
                RefreshToken.putRefreshToken(refreshToken, id);
                LoginResponseDto responseDto = LoginResponseDto.builder()
                                                .username(username)
                                                .accessToken(accessToken)
                                                .refreshToken(refreshToken)
                                                .build();
                return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDataDto.set("OK", responseDto));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseMessageDto.set(badRequestError, "아이디와 비밀번호가 일치하지않습니다."));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ResponseMessageDto.set(badRequestError, "아이디가 존재하지 않습니다."));
    }

    public ResponseEntity<?> deleteUser(HttpServletRequest request){
        Long loginUserId = getUserId(request);
        Optional<User> existingUser = userRepository.findById(loginUserId);
        if(existingUser.isPresent()){
                User user = existingUser.get();
                GetUserInfoFromUsernameResponseDto response = GetUserInfoFromUsernameResponseDto.builder()
                                                                .email(user.getEmail())
                                                                .username(user.getUsername())
                                                                .build();
                userRepository.deleteById(loginUserId);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지않는 유저입니다."));
    }


    // 이름을 통해서 사용자의 정보를 알아내는 메서드
    public ResponseEntity<?> getUserInfoFromToken(HttpServletRequest request){
        final String token = request.getHeader("Authorization");
        String id = null;
        if(token != null && !token.isEmpty()){
            String jwtToken = token.substring(7);
            id = jwtProvider.getUserIdFromToken(jwtToken);
        }
        Optional<User> existingUser = userRepository.findById(Long.parseLong(id));
        if (existingUser.isPresent()) {
            GetUserInfoFromUsernameResponseDto responseDto = GetUserInfoFromUsernameResponseDto.builder()
                                                            .email(existingUser.get().getEmail())
                                                            .username(existingUser.get().getUsername())
                                                            .build();
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", responseDto));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ResponseMessageDto.set(badRequestError, "해당하는 정보가 없습니다."));
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
