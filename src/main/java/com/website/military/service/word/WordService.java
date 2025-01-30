package com.website.military.service.word;

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
import com.website.military.repository.UserRepository;
import com.website.military.repository.WordRepository;
import com.website.military.repository.WordSetsMappingRepository;
import com.website.military.repository.WordSetsRepository;
import com.website.military.service.wordsets.WordSetService;

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
    private WordSetService wordSetService;
    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    public ResponseEntity<?> addWordToWordSet(Long setId, AddWordToWordSetDto dto, HttpServletRequest request){
        Long wordSetId = setId;
        String word = dto.getWord();
        String meaning = dto.getMeaning();
        Optional<WordSets> existingWordSets = wordSetsRepository.findBySetId(wordSetId);
        if(existingWordSets.isPresent()){
            Long id = wordSetService.getUserId(request);
            Optional<User> user = userRepository.findById(id);
            if(user.isPresent()){
                Long userId = user.get().getUserId();
                if(userId.equals(existingWordSets.get().getUser().getUserId())){
                    WordSets wordSets = existingWordSets.get();
                    Word newWord = new Word();
                    newWord.setWord(word);
                    newWord.setMeaning(meaning);
                    wordRepository.save(newWord);
                    WordSetMapping mapping = new WordSetMapping();
                    mapping.setWord(newWord);
                    mapping.setWordsets(wordSets);
                    wordSetsMappingRepository.save(mapping);
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",newWord));
                }else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어셋을 만든 사람과 사용하는 사용자가 다릅니다."));
                }
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "토큰에 해당하는 사용자가 없습니다."));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어셋의 입력이 잘못되었습니다."));
    }
}
