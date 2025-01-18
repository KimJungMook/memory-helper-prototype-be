package com.website.military.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.website.military.domain.Entity.User;
import com.website.military.domain.dto.SignUpDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.repository.UserRepository;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    // 회원가입 하는데 사용하는 메서드
    public ResponseEntity<?> signUp(SignUpDto dto){
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if(existingUser.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseMessageDto.set("BAD_REQUEST", "존재하는 아이디가 있습니다."));
        }
        
        try{
            User user = new User(dto.getUsername(), dto.getEmail(), dto.getPassword());
            user.setCreatedAt(new Date());
            userRepository.save(user);
        return ResponseEntity.ok(ResponseDataDto.set("OK", user));
        }catch(Exception e){
            e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set("INTERNAL_SERVER_ERROR", "서버 에러"));
        }
    }
}
