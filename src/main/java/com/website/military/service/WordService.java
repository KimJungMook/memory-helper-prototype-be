package com.website.military.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.website.military.domain.Entity.User;
import com.website.military.domain.Entity.Word;
import com.website.military.domain.Entity.WordSetMapping;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.gemini.request.GeminiRequestDto;
import com.website.military.domain.dto.gemini.response.GeminiResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.domain.dto.word.request.ExistWordDto;
import com.website.military.domain.dto.word.request.UpdateMeaningDto;
import com.website.military.domain.dto.word.response.AddWordToWordSetResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.domain.dto.word.response.UpdateMeaningResponseDto;
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

    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.url}")
    private String apiUrl;

    @Value("${gemini.api_key}")
    private String apiKey;
    @
    Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;
// 수정사항 발생 -> 같은 단어지만 사용자가 다르게 뜻을 넣을 수 있기에, word와 userId로 search하는 방법으로 찾아야할듯.(02-04)
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
                                                    .id(existingWord.getWordId())
                                                    .word(existingWord.getWord())
                                                    .noun(existingWord.getNoun())
                                                    .verb(existingWord.getVerb())
                                                    .adjective(existingWord.getAdjective())
                                                    .adverb(existingWord.getAdverb())
                                                    .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("EXIST", dtos));
                }
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 유저가 없습니다."));
            }
        }

        try {
            String s = getAIDescription(word);
            String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
            JSONObject object = new JSONObject(cleanJson);
            JSONObject newObject = object.getJSONObject(word);
            List<List<String>> meanings = new ArrayList<>(); // List의 List를 넣는게 맞는지 02-02에 체크하기.
            meanings.add(new ArrayList<>());
            meanings.add(new ArrayList<>());
            meanings.add(new ArrayList<>());
            meanings.add(new ArrayList<>());
            JSONArray noun = newObject.getJSONArray("noun");
            JSONArray verb = newObject.getJSONArray("verb");
            JSONArray adjective = newObject.getJSONArray("adjective");
            JSONArray adverb = newObject.getJSONArray("adverb");
            int nounLength = noun.length();
            int verbLength = verb.length();
            int adjectiveLength = adjective.length();
            int adverbLength = adverb.length();

            for(int i=0;i<nounLength;i++){
                meanings.get(0).add(noun.getString(i));    
            }

            for(int i=0;i<verbLength;i++){
                meanings.get(1).add(verb.getString(i));    
            }

            for(int i=0;i<adjectiveLength;i++){
                meanings.get(2).add(adjective.getString(i));    
            }
            for(int i=0;i<adverbLength;i++){
                meanings.get(3).add(adverb.getString(i));    
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", meanings)); 
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseMessageDto.set(internalError, "서버 에러"));
        }

    }
    // 정확도가 조금 떨어지긴 함. (02. 05.)
    public ResponseEntity<?> correctSpelling(String word){
        String returnWord = correctWordCheck(word);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", returnWord));
    }

    public ResponseEntity<?> addWordToWordSet(Long setId, AddWordToWordSetDto dto, HttpServletRequest request){
        Long wordSetId = setId;
        String word = dto.getWord();
        List<String> noun = dto.getNoun();
        List<String> verb = dto.getVerb();
        List<String> adjective = dto.getAdjective();
        List<String> adverb = dto.getAdverb();
        Optional<WordSets> existingWordSets = wordSetsRepository.findBySetId(wordSetId);

        if(existingWordSets.isPresent()){
            Long id = authService.getUserId(request);
            Optional<User> user = userRepository.findById(id);

            if(user.isPresent()){
                Long userId = user.get().getUserId();
                User existingUser = user.get();

                if(userId.equals(existingWordSets.get().getUser().getUserId())){
                    WordSets wordSets = existingWordSets.get();
                    Word newWord = new Word(word, noun, verb, adjective, adverb, existingUser);
                    Optional<Word> words = wordRepository.findByWord(word);
                    if(words.isPresent()){
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "단어가 이미 존재합니다."));
                    }else{
                        wordRepository.save(newWord);
                        WordSetMapping mapping = new WordSetMapping();
                        mapping.setWord(newWord);
                        mapping.setWordsets(wordSets);
                        wordSetsMappingRepository.save(mapping);
                        AddWordToWordSetResponseDto dtos = AddWordToWordSetResponseDto.builder()
                                                            .wordId(newWord.getWordId())
                                                            .word(newWord.getWord())
                                                            .noun(newWord.getNoun())
                                                            .verb(newWord.getVerb())
                                                            .adjective(newWord.getAdjective())
                                                            .adverb(newWord.getAdverb())
                                                            .build();

                        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",dtos));
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

    // QA 테스트 미흡 ( 2.3.) -> 수정하면 시간 변경하도록.
    public ResponseEntity<?> updateMeaning(Long id, UpdateMeaningDto dto,HttpServletRequest request){
        Long userId = authService.getUserId(request);
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            Optional<Word> existingWord = wordRepository.findById(id);
            if(existingWord.isPresent()){
                Word words = existingWord.get();
                if(userId.equals(words.getUser().getUserId())){
                    Optional.ofNullable(dto.getNoun())
                    .ifPresent(words::setNoun);
                    Optional.ofNullable(dto.getVerb())
                    .ifPresent(words::setVerb);
                    Optional.ofNullable(dto.getAdjective())
                    .ifPresent(words::setAdjective);
                    Optional.ofNullable(dto.getAdverb())
                    .ifPresent(words::setAdverb);
                    words.setUpdatedAt(Instant.now());
                    wordRepository.save(words);
                    UpdateMeaningResponseDto dtos = UpdateMeaningResponseDto.builder()
                                                    .wordId(words.getWordId())
                                                    .noun(words.getNoun())
                                                    .verb(words.getVerb())
                                                    .adjective(words.getAdjective())
                                                    .adverb(words.getAdverb())
                                                    .build();            
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",dtos));
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "단어를 만든 사람과 사용하는 사용자가 다릅니다."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "해당하는 단어가 없습니다."));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "토큰에 해당하는 사용자가 없습니다."));
    }

    public String getAIDescription(String word){
        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = new GeminiRequestDto();

        String processedSentence = word + " 뜻을 json형식으로 보여줘. 품사는 명사, 동사, 형용사, 부사 순서로 알려주면 돼. 뜻이 없으면 빈칸으로 만들어줘. 각각의 개수는 2~3개로 해줘.";
        request.createGeminiReqDto(processedSentence);
        String description = "";
        try {
            GeminiResponseDto response = restTemplate.postForObject(requestUrl, request, GeminiResponseDto.class);
            if(response != null){
                description = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return description;
    }

    public String correctWordCheck(String word){
        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = new GeminiRequestDto();
        String processedSentence = word + " 의 철자가 틀리지 않으면 그 단어를 그대로 대답하고 틀렸으면, 가장 비슷한 단어를 단어만 대답해줘.";
        request.createGeminiReqDto(processedSentence);
        String description = "";
        try {
            GeminiResponseDto response = restTemplate.postForObject(requestUrl, request, GeminiResponseDto.class);
            if(response != null){
                description = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        description =  description.trim();
        return description;
    }
}
