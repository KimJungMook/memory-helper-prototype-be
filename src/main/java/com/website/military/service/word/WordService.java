package com.website.military.service.word;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.website.military.domain.Entity.Word;
import com.website.military.domain.Entity.WordSetMapping;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.repository.WordRepository;
import com.website.military.repository.WordSetsMappingRepository;
import com.website.military.repository.WordSetsRepository;

@Service
public class WordService {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordSetsRepository wordSetsRepository;

    @Autowired
    private WordSetsMappingRepository wordSetsMappingRepository;
    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;
// user_id와  맞는지 인증하는거 해야함. -> 25.1.30 여기서부터시작하기.
    public ResponseEntity<?> addWordToWordSet(AddWordToWordSetDto dto){
        Long wordSetId = dto.getSetId();
        String word = dto.getWord();
        String meaning = dto.getMeaning();
        Optional<WordSets> existingWordSets = wordSetsRepository.findBySetId(wordSetId);
        if(existingWordSets.isPresent()){
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
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어셋의 입력이 잘못되었습니다."));
    }
}
