package com.website.military.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.website.military.domain.Entity.User;
import com.website.military.domain.Entity.Word;
import com.website.military.domain.Entity.WordSetMapping;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.domain.dto.word.request.ExistWordDto;
import com.website.military.domain.dto.word.response.AddWordToWordSetResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.repository.UserRepository;
import com.website.military.repository.WordRepository;
import com.website.military.repository.WordSetsMappingRepository;
import com.website.military.repository.WordSetsRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class WordService {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordSetsRepository wordSetsRepository;

    @Autowired
    private WordSetsMappingRepository wordSetsMappingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    public ResponseEntity<?> existWord(ExistWordDto dto, HttpServletRequest request){
        String word = dto.getWord();
        Long id = authService.getUserId(request);
        Optional<User> user = userRepository.findById(id);
        Optional<Word> words = wordRepository.findByWord(word);
        if(words.isPresent()){
            if(user.isPresent()){
                User existingUser = user.get();
                Word existingWord = words.get();
                if(existingUser.equals(existingWord.getUser())){
                    ExistWordResponseDto dtos = ExistWordResponseDto.builder()
                                                    .word(existingWord.getWord())
                                                    .meaning(existingWord.getMeaning())
                                                    .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("EXIST", dtos));
                }else{
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseMessageDto.set("OK", "해당하는 단어가 DB에 없습니다."));
                }
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 유저가 없습니다."));
            }
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(ResponseMessageDto.set("OK", "해당하는 단어가 DB에 없습니다."));
        }

    }

    public ResponseEntity<?> addWordToWordSet(Long setId, AddWordToWordSetDto dto, HttpServletRequest request){
        Long wordSetId = setId;
        String word = dto.getWord();
        List<String> meaning = dto.getMeaning();
        Optional<WordSets> existingWordSets = wordSetsRepository.findBySetId(wordSetId);
        if(existingWordSets.isPresent()){
            Long id = authService.getUserId(request);
            Optional<User> user = userRepository.findById(id);
            if(user.isPresent()){
                Long userId = user.get().getUserId();
                User existingUser = user.get();
                if(userId.equals(existingWordSets.get().getUser().getUserId())){
                    WordSets wordSets = existingWordSets.get();
                    Word newWord = new Word();
                    newWord.setWord(word);
                    newWord.setMeaning(meaning);
                    newWord.setUser(existingUser);
                    wordRepository.save(newWord);
                    WordSetMapping mapping = new WordSetMapping();
                    mapping.setWord(newWord);
                    mapping.setWordsets(wordSets);
                    wordSetsMappingRepository.save(mapping);
                    AddWordToWordSetResponseDto dtos = AddWordToWordSetResponseDto.builder()
                                                        .wordId(newWord.getWordId())
                                                        .word(newWord.getWord())
                                                        .meaning(newWord.getMeaning())
                                                        .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",dtos));
                }else{
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "단어셋을 만든 사람과 사용하는 사용자가 다릅니다."));
                }
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어셋의 입력이 잘못되었습니다."));
    }
}
