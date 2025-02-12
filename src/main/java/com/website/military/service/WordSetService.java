package com.website.military.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.website.military.domain.Entity.GptWord;
import com.website.military.domain.Entity.GptWordSetMapping;
import com.website.military.domain.Entity.User;
import com.website.military.domain.Entity.Word;
import com.website.military.domain.Entity.WordSetMapping;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.domain.dto.word.response.AddWordToWordSetResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.domain.dto.wordsets.request.WordSetsDto;
import com.website.military.domain.dto.wordsets.response.DeleteResponseDto;
import com.website.military.domain.dto.wordsets.response.DetachResponse;
import com.website.military.domain.dto.wordsets.response.GetWordsBySetIdResponse;
import com.website.military.domain.dto.wordsets.response.RegisterResponseDto;
import com.website.military.domain.dto.wordsets.response.WordSetsResponseDto;
import com.website.military.repository.GptWordRepository;
import com.website.military.repository.GptWordSetMappingRepository;
import com.website.military.repository.UserRepository;
import com.website.military.repository.WordRepository;
import com.website.military.repository.WordSetsMappingRepository;
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
    private WordSetsMappingRepository wordSetsMappingRepository;
    @Autowired
    private GptWordSetMappingRepository gptWordSetMappingRepository;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private GptWordRepository gptWordRepository;
    @Autowired
    private AuthService authService;

    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    public ResponseEntity<?> getWordSets(HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            List<WordSets> existingWordSets = wordSetsRepository.findByUser_UserId(userId);
            List<WordSetsResponseDto> responses = new ArrayList<>();
            for(WordSets sets : existingWordSets){
                WordSetsResponseDto response = WordSetsResponseDto.builder()
                .setId(sets.getSetId())
                .setName(sets.getSetName())
                .createdAt(sets.getCreatedAt())
                .build();
                responses.add(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",responses));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
    }

    public ResponseEntity<?> getWordsBySetId(Long id, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            List<WordSetMapping> mappings = wordSetsMappingRepository.findAllByWordsets_SetId(id);
            List<GetWordsBySetIdResponse> words = new ArrayList<>();
            for(WordSetMapping mapping : mappings){
                Word response = mapping.getWord();
                GetWordsBySetIdResponse responses = GetWordsBySetIdResponse.builder()
                .id(response.getWordId())
                .word(response.getWord())
                .noun(response.getNoun())
                .verb(response.getVerb())
                .adjective(response.getAdjective())
                .adverb(response.getAdverb())
                .createdAt(response.getCreateAt())
                .build();
                words.add(responses);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", words));

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
    }


    public ResponseEntity<?> registerWordSets(WordSetsDto dto, HttpServletRequest request){
        Optional<WordSets> existingWordSets = wordSetsRepository.findBysetName(dto.getSetName());
        if(existingWordSets.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "이미 존재한 세트 이름"));
        }
        WordSets wordSets = new WordSets(dto.getSetName());
        try{
            Long userId = authService.getUserId(request);
            Optional<User> existingUser = userRepository.findById(userId);
            if(existingUser.isPresent()){
                User user = existingUser.get();
                wordSets.setUser(user);
                wordSetsRepository.save(wordSets);
                RegisterResponseDto response = RegisterResponseDto.builder()
                .setId(wordSets.getSetId())
                .setName(wordSets.getSetName())
                .build();
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response)); // 여기서부터 다시.
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseMessageDto.set(internalError, "서버 에러"));
        }
    }

    public ResponseEntity<?> assignWordToSet(Long setId, Long wordId, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            Optional<WordSets> existingWordSets = wordSetsRepository.findBySetId(setId);
            if(existingWordSets.isPresent()){
                Optional<Word> existingWord = wordRepository.findById(wordId);
                if(existingWord.isPresent()){
                    Word word = existingWord.get();
                    WordSets sets = existingWordSets.get();
                    WordSetMapping mapping = new WordSetMapping();
                    mapping.setWord(word);
                    mapping.setWordsets(sets);
                    wordSetsMappingRepository.save(mapping);
                    ExistWordResponseDto response = ExistWordResponseDto.builder()
                    .id(word.getWordId())
                    .noun(word.getNoun())
                    .verb(word.getVerb())
                    .adjective(word.getAdjective())
                    .adverb(word.getAdverb())
                    .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
                }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하는 단어가 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하는 단어셋이 없습니다."));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
    }

    public ResponseEntity<?> addWordToWordSet(Long setId, AddWordToWordSetDto dto, HttpServletRequest request){
        String word = dto.getWord();
        List<String> noun = dto.getNoun();
        List<String> verb = dto.getVerb();
        List<String> adjective = dto.getAdjective();
        List<String> adverb = dto.getAdverb();
        Optional<WordSets> existingWordSets = wordSetsRepository.findBySetId(setId);

        if(existingWordSets.isPresent()){
            Long userId = authService.getUserId(request);
            Optional<User> user = userRepository.findById(userId);

            if(user.isPresent()){
                User existingUser = user.get();
                if(userId.equals(existingWordSets.get().getUser().getUserId())){
                    WordSets wordSets = existingWordSets.get();
                    Optional<Word> existingWord = wordRepository.findByWordAndUser_UserId(word, userId); 
                    if(existingWord.isPresent()){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어가 이미 존재합니다."));
                    }else{
                        Word Word = new Word(word, noun, verb, adjective, adverb, existingUser);
                        wordRepository.save(Word);
                        WordSetMapping mapping = new WordSetMapping();
                        mapping.setWord(Word);
                        mapping.setWordsets(wordSets);
                        wordSetsMappingRepository.save(mapping);
                        AddWordToWordSetResponseDto response = AddWordToWordSetResponseDto.builder()
                        .wordId(Word.getWordId())
                        .word(Word.getWord())
                        .noun(Word.getNoun())
                        .verb(Word.getVerb())
                        .adjective(Word.getAdjective())
                        .adverb(Word.getAdverb())
                        .build();

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
                    }
                }else{
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "단어셋을 만든 사람과 사용하는 사용자가 다릅니다."));
                }
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어셋의 입력이 잘못되었습니다."));
    }

    public ResponseEntity<?> addGptWordToWordSet(Long setId, AddWordToWordSetDto dto, HttpServletRequest request){
        String word = dto.getWord();
        List<String> noun = dto.getNoun();
        List<String> verb = dto.getVerb();
        List<String> adjective = dto.getAdjective();
        List<String> adverb = dto.getAdverb();
        Optional<WordSets> existingWordSets = wordSetsRepository.findBySetId(setId);

        if(existingWordSets.isPresent()){
            Long userId = authService.getUserId(request);
            Optional<User> user = userRepository.findById(userId);

            if(user.isPresent()){
                User existingUser = user.get();
                if(userId.equals(existingWordSets.get().getUser().getUserId())){ 
                    WordSets wordSets = existingWordSets.get();
                    Optional<GptWord> existingWord = gptWordRepository.findByWordAndUser_UserId(word, userId); 
                    if(existingWord.isPresent()){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어가 이미 존재합니다."));
                    }else{
                        GptWord Word = new GptWord(word, noun, verb, adjective, adverb, existingUser);
                        gptWordRepository.save(Word);
                        GptWordSetMapping mapping = new GptWordSetMapping();
                        mapping.setGptword(Word);
                        mapping.setWordsets(wordSets);
                        gptWordSetMappingRepository.save(mapping);
                        AddWordToWordSetResponseDto response = AddWordToWordSetResponseDto.builder()
                        .wordId(Word.getGptWordId())
                        .word(Word.getWord())
                        .noun(Word.getNoun())
                        .verb(Word.getVerb())
                        .adjective(Word.getAdjective())
                        .adverb(Word.getAdverb())
                        .build();

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
                    }
                }else{
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "단어셋을 만든 사람과 사용하는 사용자가 다릅니다."));
                }
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어셋의 입력이 잘못되었습니다."));
    }

    public ResponseEntity<?> changeSetName(Long id, String setName,HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            Optional<WordSets> existingWordSets = wordSetsRepository.findById(id);
            if(existingWordSets.isPresent()){
                if(userId.equals(existingWordSets.get().getUser().getUserId())){
                    WordSets sets = existingWordSets.get();
                    sets.setSetName(setName);
                    wordSetsRepository.save(sets);
                    RegisterResponseDto response = RegisterResponseDto.builder()
                    .setId(id)
                    .setName(setName)
                    .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 세트입니다."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 유저입니다."));
    }

    public ResponseEntity<?> deleteWordSets(Long id, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            Optional<WordSets> existingWordSets = wordSetsRepository.findById(id);
            if(existingWordSets.isPresent()){
                WordSets sets = existingWordSets.get();
                User user = existingUser.get();
                if(sets.getUser().equals(user)){
                    wordSetsRepository.deleteById(id);
                    DeleteResponseDto response = DeleteResponseDto.builder()
                    .id(id)
                    .setName(sets.getSetName())
                    .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 세트입니다."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 유저입니다."));
    }

    public ResponseEntity<?> detachWordFromSet(Long setId, Long wordId, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            Optional<WordSets> existingWordSets = wordSetsRepository.findById(setId);
            if (existingWordSets.isPresent()) {
                WordSets sets = existingWordSets.get();
                if(existingUser.get().equals(sets.getUser())){
                    Optional<WordSetMapping> mappings = wordSetsMappingRepository.findByWord_WordIdAndWordsets_SetId(wordId, setId);
                    if(mappings.isPresent()){
                        Long deleteId = mappings.get().getId();
                        Word Word = mappings.get().getWord();
                        DetachResponse response = DetachResponse.builder()
                        .id(Word.getWordId())
                        .noun(Word.getNoun())
                        .verb(Word.getVerb())
                        .adjective(Word.getAdjective())
                        .adverb(Word.getAdverb())
                        .build();
                        wordSetsMappingRepository.deleteById(deleteId);
                        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("DELETE", response));
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 세트입니다."));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 유저입니다."));

    }
}
