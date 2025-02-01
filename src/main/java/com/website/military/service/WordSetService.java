package com.website.military.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.website.military.domain.Entity.User;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.wordsets.request.WordSetsDto;
import com.website.military.domain.dto.wordsets.response.WordSetsResponseDto;
import com.website.military.repository.UserRepository;
import com.website.military.repository.WordSetsRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordSetService {
    @Autowired
    private WordSetsRepository wordSetsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    public ResponseEntity<?> getWordSets(HttpServletRequest request){
        Long id = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()){
            Long userId = existingUser.get().getUserId();
            List<WordSets> existingWordSets = wordSetsRepository.findByUser_UserId(userId);
            List<WordSetsResponseDto> dtos = new ArrayList<>();
            for(WordSets sets : existingWordSets){
                WordSetsResponseDto dto = WordSetsResponseDto.builder()
                                            .setId(sets.getSetId())
                                            .setName(sets.getSetName())
                                            .createdAt(sets.getCreatedAt())
                                            .build();
                dtos.add(dto);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",dtos));
        }
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", ""));
    }

    public ResponseEntity<?> RegisterWordSets(WordSetsDto dto, HttpServletRequest request){
        Optional<WordSets> existingWordSets = wordSetsRepository.findBysetName(dto.getSetName());
        if(existingWordSets.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "이미 존재한 세트 이름"));
        }
        WordSets wordSets = new WordSets(dto.getSetName());
        try{
            Long id = authService.getUserId(request);
            Optional<User> existingUser = userRepository.findById(id);
            if(existingUser.isPresent()){
                User user = existingUser.get();
                wordSets.setUser(user);
                wordSetsRepository.save(wordSets);

                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", wordSets)); // 여기서부터 다시.
            }
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseMessageDto.set(internalError, "서버 에러"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "세트가 만들어지지 않았습니다."));
    }

}
