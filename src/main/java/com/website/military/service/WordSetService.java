package com.website.military.service;

import java.time.Instant;
import java.util.ArrayList;
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
import com.website.military.domain.dto.response.WordClassResponse;
import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.domain.dto.word.request.UpdateMeaningDto;
import com.website.military.domain.dto.word.response.AddWordToWordSetResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.domain.dto.wordsets.request.WordSetsDto;
import com.website.military.domain.dto.wordsets.response.DeleteResponseDto;
import com.website.military.domain.dto.wordsets.response.DetachResponse;
import com.website.military.domain.dto.wordsets.response.GetWordsBySetIdFinalResponse;
import com.website.military.domain.dto.wordsets.response.GetWordsBySetIdResponse;
import com.website.military.domain.dto.wordsets.response.RegisterResponseDto;
import com.website.military.domain.dto.wordsets.response.WordSetsResponseDto;
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
    private WordRepository wordRepository;
    @Autowired
    private AuthService authService;

    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    // 유저가 쓴 단어세트를 모두 불러오는 메서드
    public ResponseEntity<?> getWordSets(HttpServletRequest request){
        Long userId = authService.getUserId(request);
        List<WordSets> existingWordSets = wordSetsRepository.findByUser_UserId(userId);
        List<WordSetsResponseDto> responses = new ArrayList<>();
        if(!existingWordSets.isEmpty()){
            for(WordSets sets : existingWordSets){
                WordSetsResponseDto response = new WordSetsResponseDto(sets.getSetId(), sets.getSetName(), sets.getCreatedAt(), sets.getWordCount() ,sets.getExams()); // 여기를 tests로 넣었는데, 리스트가 아니고 테스트 하나만 나오게 할거면 변경 필요할 듯.
                responses.add(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",responses));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }
    }

    // 단어세트에 있는 단어 불러오는 메서드
    public ResponseEntity<?> getWordsBySetId(Long setId, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<WordSets> wordsets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if(wordsets.isPresent()){
            List<WordSetMapping> wordSetsMappings = wordSetsMappingRepository.findAllByWordsets_SetId(setId);
            List<GetWordsBySetIdResponse> words = new ArrayList<>();
            for(WordSetMapping mapping : wordSetsMappings){ // 둘다 비어져 있을 때는 어떻게 하는게좋을지.
                Word response = mapping.getWord();
                GetWordsBySetIdResponse responses = new GetWordsBySetIdResponse(response.getWordId(), response.getWord(), response.getNoun(), 
                response.getVerb(), response.getAdjective(), response.getAdverb(), response.getCreateAt());
                words.add(responses);
            }
            GetWordsBySetIdFinalResponse response = GetWordsBySetIdFinalResponse.builder()
            .list(words)
            .name(wordsets.get().getSetName())
            .createdAt(wordsets.get().getCreatedAt())
            .build();
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

    // 세트 만드는 메서드
    public ResponseEntity<?> registerWordSets(WordSetsDto dto, HttpServletRequest request){
        Optional<WordSets> existingWordSets = wordSetsRepository.findBysetName(dto.getSetName());
        if(existingWordSets.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }
        WordSets wordSets = new WordSets(dto.getSetName());
        Long userId = authService.getUserId(request);
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            User user = existingUser.get();
            wordSets.setUser(user);
            try {
                wordSetsRepository.save(wordSets);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set(internalError, "서버 에러"));
            }
            RegisterResponseDto response = new RegisterResponseDto(wordSets.getSetId(), wordSets.getSetName());
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response)); // 여기서부터 다시.
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "잘못된 접근입니다."));
    }

    // // 이미 존재한 단어를 단어장에 넣기
    // public ResponseEntity<?> assignWordToSet(Long setId, Long wordId, HttpServletRequest request, boolean isGpt){
    //     Long userId = authService.getUserId(request);
    //     Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
    //     if(existingWordSets.isPresent() && existingWordSets.get().getWordCount() <= 50){
    //         if(isGpt){
    //             Optional<GptWordSetMapping> existingGptWordSetMappings = gptWordSetMappingRepository.findByGptword_GptWordIdAndWordsets_SetId(wordId, setId);
    //             if(existingGptWordSetMappings.isPresent()){ // gpt단어가 단어장에 매핑이 되어져 있는지, 유저 단어가 단어장에 매핑이 되어져 있는지 체크
    //                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    //             }
    //             Optional<GptWord> existingGptWord = gptWordRepository.findById(wordId);
    //             if(existingGptWord.isPresent()){
    //                 GptWord gptWord = existingGptWord.get();
    //                 WordSets sets = existingWordSets.get();
    //                 GptWordSetMapping mapping = new GptWordSetMapping();
    //                 mapping.setGptword(gptWord);
    //                 mapping.setWordsets(sets);
    //                 try {
    //                     gptWordSetMappingRepository.save(mapping);
    //                     wordSetsRepository.incrementWordCount(setId);   
    //                 } catch (Exception e) {
    //                     e.printStackTrace();
    //                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                     .body(ResponseMessageDto.set(internalError, "서버 에러"));
    //                 }
    //                 ExistWordResponseDto response = new ExistWordResponseDto(gptWord.getGptWordId(), gptWord.getWord(), gptWord.getNoun(), gptWord.getVerb(), 
    //                 gptWord.getAdjective(), gptWord.getAdverb(), isGpt);
    //                 return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
    //             }
    //         }else{
    //             Optional<WordSetMapping> existingWordSetMappings = wordSetsMappingRepository.findByWord_WordIdAndWordsets_SetId(wordId, setId);
    //             if(existingWordSetMappings.isPresent()){ // gpt단어가 단어장에 매핑이 되어져 있는지, 유저 단어가 단어장에 매핑이 되어져 있는지 체크
    //                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    //             }
    //             Optional<Word> existingWord = wordRepository.findByWordIdAndUser_UserId(wordId, userId);
    //             if(existingWord.isPresent()){
    //                 Word word = existingWord.get();
    //                 WordSets sets = existingWordSets.get();
    //                 WordSetMapping mapping = new WordSetMapping();
    //                 mapping.setWord(word);
    //                 mapping.setWordsets(sets);
    //                 try {
    //                     wordSetsMappingRepository.save(mapping);
    //                     wordSetsRepository.incrementWordCount(setId);    
    //                 } catch (Exception e) {
    //                     e.printStackTrace();
    //                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                     .body(ResponseMessageDto.set(internalError, "서버 에러"));
    //                 }
    //                 ExistWordResponseDto response = new ExistWordResponseDto(word.getWordId(), word.getWord(), word.getNoun(), word.getVerb(), 
    //                 word.getAdjective(), word.getAdverb(), isGpt);
    //                 return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
    //             }   
    //         }
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    //     }
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    // }

    // 단어를 단어장에 넣기 (아직 존재하지 않는 단어)
    public ResponseEntity<?> addWordToWordSet(Long setId, AddWordToWordSetDto dto, HttpServletRequest request){
        String word = dto.getWord();
        List<String> noun = new ArrayList<>();
        List<String> verb = new ArrayList<>();
        List<String> adjective = new ArrayList<>();
        List<String> adverb = new ArrayList<>();
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if(existingWordSets.isPresent() && existingWordSets.get().getWordCount() <= 50){ // 단어세트의 총 개수는 50개
            User existingUser = existingWordSets.get().getUser();
            WordSets wordSets = existingWordSets.get();
            for(WordClassResponse r : dto.getMeaning()){
                if(r.getType().equals("noun")){
                    noun.add(r.getValue());
                }else if(r.getType().equals("verb")){
                    verb.add(r.getValue());
                }else if(r.getType().equals("adjective")){
                    adjective.add(r.getValue());
                }else if(r.getType().equals("adverb")){
                    adverb.add(r.getValue());
                }else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
                }
            }
            Word Word = new Word(word, noun, verb, adjective, adverb, existingUser);
            try {
                wordRepository.save(Word);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set(internalError, "서버 에러"));
            }
            AddWordToWordSetResponseDto response = new AddWordToWordSetResponseDto(Word.getWordId(), Word.getWord(), Word.getNoun(), 
            Word.getVerb(), Word.getAdjective(), Word.getAdverb());   
            WordSetMapping mapping = new WordSetMapping(Word, wordSets);           
            try {
                wordSetsMappingRepository.save(mapping);
                wordSetsRepository.incrementWordCount(setId);                   
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set(internalError, "서버 에러"));
            }     
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "잘못된 접근입니다."));
        
    }

    // 세트의 이름을 바꾸고 싶을 때 사용하는 메서드
    public ResponseEntity<?> changeSetName(Long setId, String setName,HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if(existingWordSets.isPresent()){
            WordSets sets = existingWordSets.get();
            sets.setSetName(setName);
            try {
                wordSetsRepository.save(sets);   
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set(internalError, "서버 에러"));
            }
            RegisterResponseDto response = new RegisterResponseDto(setId, setName);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

    // 단어 Set를 없애는 메서드
    public ResponseEntity<?> deleteWordSets(Long setId, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if(existingWordSets.isPresent()){
            WordSets sets = existingWordSets.get();
            try {
                wordSetsRepository.deleteById(setId);               
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessageDto.set(internalError, "서버 에러"));
            }
            DeleteResponseDto response = new DeleteResponseDto(setId, sets.getSetName());

            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

    // 단어장에 있는 단어를 없애고 싶을 때 사용하는 메서드
    public ResponseEntity<?> detachWordFromSet(Long setId, Long wordId, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if (existingWordSets.isPresent() && existingWordSets.get().getExams().isEmpty()) {
            // if(isGpt){
            //     Optional<GptWordSetMapping> mappings = gptWordSetMappingRepository.findByGptword_GptWordIdAndWordsets_SetId(wordId, setId);
            //     if(mappings.isPresent()){
            //         Long deleteId = mappings.get().getId();
            //         GptWord Word = mappings.get().getGptword();
            //         DetachResponse response = new DetachResponse(Word.getGptWordId(), Word.getWord(), Word.getNoun(), Word.getVerb(),
            //         Word.getAdjective(), Word.getAdjective());
            //         try {
            //             wordSetsMappingRepository.deleteById(deleteId);   
            //             wordSetsRepository.decrementWordCount(setId);
            //         } catch (Exception e) {
            //             e.printStackTrace();
            //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            //             .body(ResponseMessageDto.set(internalError, "서버 에러"));
            //         }

            //         return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            //     }
            // }else{                
            Optional<WordSetMapping> mappings = wordSetsMappingRepository.findByWord_WordIdAndWordsets_SetId(wordId, setId);
            if(mappings.isPresent()){
                Long deleteId = mappings.get().getId();
                Word Word = mappings.get().getWord();
                DetachResponse response = new DetachResponse(Word.getWordId(), Word.getWord(), Word.getNoun(), Word.getVerb(),
                Word.getAdjective(), Word.getAdverb());
                try {
                    wordSetsMappingRepository.deleteById(deleteId);  
                    wordSetsRepository.decrementWordCount(setId);
                    // wordRepository.deleteById(deleteId); // 일단 고민을 해봐야할 부분으로 보임. 단어를 없애는 게 맞는지?
                    } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessageDto.set(internalError, "서버 에러"));
                }
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
                // }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));

    }

    public ResponseEntity<?> patchWordFromSet(Long setId, Long wordId, UpdateMeaningDto dto, HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if (existingWordSets.isPresent() && existingWordSets.get().getExams().isEmpty()) {
            Optional<WordSetMapping> mappings = wordSetsMappingRepository.findByWord_WordIdAndWordsets_SetId(wordId, setId);
            if(mappings.isPresent()){
                Word word = mappings.get().getWord();
                System.out.println(word.getWord());
                List<String> verb = new ArrayList<>();
                List<String> noun = new ArrayList<>();
                List<String> adjective = new ArrayList<>();
                List<String> adverb = new ArrayList<>();

                for(WordClassResponse response : dto.getMeaning()){
                    String type = response.getType();
                    String value = response.getValue();
                    if(type.equals("verb")){
                        verb.add(value);
                    }else if(type.equals("noun")){
                        noun.add(value);
                    }else if(type.equals("adjective")){
                        adjective.add(value);
                    }else if(type.equals("adverb")){
                        adverb.add(value);
                    }else{
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
                    }
                }

                word.setVerb(verb);
                word.setNoun(noun);
                word.setAdjective(adjective);
                word.setAdverb(adverb);
                ExistWordResponseDto response = new ExistWordResponseDto(wordId, word.getWord(), noun, verb, adjective, adverb);
                try { 
                    word.setUpdatedAt(Instant.now());
                    wordRepository.save(word);
                    } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessageDto.set(internalError, "서버 에러"));
                }
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
    }
}
