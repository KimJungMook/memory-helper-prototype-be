package com.website.military.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.website.military.domain.Entity.User;
import com.website.military.domain.dto.auth.request.IdValidationDto;
import com.website.military.domain.dto.auth.request.SignInDto;
import com.website.military.domain.dto.auth.request.SignUpDto;
import com.website.military.domain.dto.auth.response.SignUpResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    // 아이디 있는지 체크하는데 사용하는 메서드
    public ResponseEntity<?> idValidate(IdValidationDto dto){
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
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
            user.setCreatedAt(new Date());
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

    public ResponseEntity<?> signIn(SignInDto dto){
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if(existingUser.isPresent()){
            if(passwordEncoder.matches(dto.getPassword(), existingUser.get().getPassword())){
                return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDataDto.set("OK", existingUser));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseMessageDto.set(badRequestError, "아이디와 비밀번호가 일치하지않습니다."));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ResponseMessageDto.set(badRequestError, "아이디가 존재하지 않습니다."));
    }

}
