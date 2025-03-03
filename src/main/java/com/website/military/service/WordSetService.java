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
                WordSetsResponseDto response = new WordSetsResponseDto(sets.getSetId(), sets.getSetName(), sets.getCreatedAt());
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
            Optional<WordSets> wordsets = wordSetsRepository.findById(id);
            if(wordsets.isPresent()){
                if(!wordsets.get().getUser().equals(existingUser.get())){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "해당하는 세트가 없습니다."));
                }
                List<WordSetMapping> wordSetsMappings = wordSetsMappingRepository.findAllByWordsets_SetId(id);
                List<GptWordSetMapping> gptWordSetMappings = gptWordSetMappingRepository.findAllByWordsets_SetId(id);
                List<GetWordsBySetIdResponse> words = new ArrayList<>();
                for(WordSetMapping mapping : wordSetsMappings){
                    Word response = mapping.getWord();
                    GetWordsBySetIdResponse responses = new GetWordsBySetIdResponse(response.getWordId(), response.getWord(), response.getNoun(), 
                    response.getVerb(), response.getAdjective(), response.getAdverb(), response.getCreateAt(), false);
                    words.add(responses);
                }
                for(GptWordSetMapping mapping : gptWordSetMappings){
                    GptWord response = mapping.getGptword();
                    GetWordsBySetIdResponse responses = new GetWordsBySetIdResponse(response.getGptWordId(), response.getWord(), response.getNoun(), 
                    response.getVerb(), response.getAdjective(), response.getAdverb(), response.getCreateAt(), true);
                    words.add(responses);
                }
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", words));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
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
                RegisterResponseDto response = new RegisterResponseDto(wordSets.getSetId(), wordSets.getSetName());
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response)); // 여기서부터 다시.
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseMessageDto.set(internalError, "서버 에러"));
        }
    }

    // 이미 존재한 단어를 단어장에 넣기
    public ResponseEntity<?> assignWordToSet(Long setId, Long wordId, HttpServletRequest request, boolean isGpt){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            Optional<WordSets> existingWordSets = wordSetsRepository.findById(setId);
            Optional<WordSetMapping> existingWordSetMaapings = wordSetsMappingRepository.findByWord_WordIdAndWordsets_SetId(wordId, setId);
            if(existingWordSets.isPresent()){
                if(existingWordSetMaapings.isPresent()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하는 단어가 단어셋에 존재합니다."));
                }else{
                    if(isGpt){
                        Optional<GptWord> existingGptWord = gptWordRepository.findById(wordId);
                        if(existingGptWord.isPresent()){
                            GptWord word = existingGptWord.get();
                            WordSets sets = existingWordSets.get();
                            GptWordSetMapping mapping = new GptWordSetMapping();
                            mapping.setGptword(word);
                            mapping.setWordsets(sets);
                            gptWordSetMappingRepository.save(mapping);
                            ExistWordResponseDto response = new ExistWordResponseDto(word.getGptWordId(), word.getWord(), word.getNoun(), word.getVerb(), 
                            word.getAdjective(), word.getAdverb(), true);
                            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
                        }
                    }
                    else{
                        Optional<Word> existingWord = wordRepository.findById(wordId);
                        if(existingWord.isPresent()){
                            Word word = existingWord.get();
                            WordSets sets = existingWordSets.get();
                            WordSetMapping mapping = new WordSetMapping();
                            mapping.setWord(word);
                            mapping.setWordsets(sets);
                            wordSetsMappingRepository.save(mapping);
                            ExistWordResponseDto response = new ExistWordResponseDto(word.getWordId(), word.getWord(), word.getNoun(), word.getVerb(), 
                            word.getAdjective(), word.getAdverb(), false);
                            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
                        }
                    }
                }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하는 단어가 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하는 단어셋이 없습니다."));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
    }

    // 단어를 단어장에 넣기 (아직 존재하지 않는 단어)
    public ResponseEntity<?> addWordToWordSet(Long setId, AddWordToWordSetDto dto, HttpServletRequest request, boolean isGpt){
        String word = dto.getWord();
        List<String> noun = dto.getNoun();
        List<String> verb = dto.getVerb();
        List<String> adjective = dto.getAdjective();
        List<String> adverb = dto.getAdverb();
        Optional<WordSets> existingWordSets = wordSetsRepository.findById(setId);

        if(existingWordSets.isPresent()){
            Long userId = authService.getUserId(request);
            Optional<User> user = userRepository.findById(userId);

            if(user.isPresent()){
                User existingUser = user.get();
                if(userId.equals(existingWordSets.get().getUser().getUserId())){
                    WordSets wordSets = existingWordSets.get();
                    if(isGpt){
                        Optional<GptWord> existingWord = gptWordRepository.findByWord(word);
                        if(!existingWord.isPresent()){
                            GptWord Word = new GptWord(word, noun, verb, adjective, adverb);
                            gptWordRepository.save(Word);
                            GptWordSetMapping mapping = new GptWordSetMapping();
                            mapping.setGptword(Word);
                            mapping.setWordsets(wordSets);
                            gptWordSetMappingRepository.save(mapping);
                            AddWordToWordSetResponseDto response = new AddWordToWordSetResponseDto(Word.getGptWordId(), Word.getWord(), Word.getNoun(), 
                            Word.getVerb(), Word.getAdjective(), Word.getAdverb());
                            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
                        }
                    }else{
                        Optional<Word> existingWord = wordRepository.findByWordAndUser_UserId(word, userId);
                        if(!existingWord.isPresent()){
                            Word Word = new Word(word, noun, verb, adjective, adverb, existingUser);
                            wordRepository.save(Word);
                            WordSetMapping mapping = new WordSetMapping();
                            mapping.setWord(Word);
                            mapping.setWordsets(wordSets);
                            wordSetsMappingRepository.save(mapping);
                            AddWordToWordSetResponseDto response = new AddWordToWordSetResponseDto(Word.getWordId(), Word.getWord(), Word.getNoun(), 
                            Word.getVerb(), Word.getAdjective(), Word.getAdverb());
                            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
                        }
                    }

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "단어가 이미 존재합니다."));
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
                    RegisterResponseDto response = new RegisterResponseDto(id, setName);
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
                    DeleteResponseDto response = new DeleteResponseDto(id, sets.getSetName());
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 세트입니다."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "존재하지 않는 유저입니다."));
    }

    public ResponseEntity<?> detachWordFromSet(Long setId, Long wordId, HttpServletRequest request, boolean isGpt){
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            Optional<WordSets> existingWordSets = wordSetsRepository.findById(setId);
            if (existingWordSets.isPresent()) {
                WordSets sets = existingWordSets.get();
                if(existingUser.get().equals(sets.getUser())){
                    if(isGpt){
                        Optional<WordSetMapping> mappings = wordSetsMappingRepository.findByWord_WordIdAndWordsets_SetId(wordId, setId);
                        if(mappings.isPresent()){
                            Long deleteId = mappings.get().getId();
                            Word Word = mappings.get().getWord();
                            DetachResponse response = new DetachResponse(Word.getWordId(), Word.getWord(), Word.getNoun(), Word.getVerb(),
                            Word.getAdjective(), Word.getAdjective());
                            wordSetsMappingRepository.deleteById(deleteId);
                            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
                        }
                    }else{
                        Optional<GptWordSetMapping> mappings = gptWordSetMappingRepository.findByGptword_GptWordIdAndWordsets_SetId(wordId, setId);
                        if(mappings.isPresent()){
                            Long deleteId = mappings.get().getId();
                            GptWord Word = mappings.get().getGptword();
                            DetachResponse response = new DetachResponse(Word.getGptWordId(), Word.getWord(), Word.getNoun(), Word.getVerb(),
                            Word.getAdjective(), Word.getAdjective());
                            wordSetsMappingRepository.deleteById(deleteId);
                            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
                        }
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
