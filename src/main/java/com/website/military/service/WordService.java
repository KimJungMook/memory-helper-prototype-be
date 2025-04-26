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

import com.website.military.domain.Entity.GptWord;
import com.website.military.domain.Entity.Word;
import com.website.military.domain.dto.gemini.request.GeminiRequestDto;
import com.website.military.domain.dto.gemini.response.GeminiResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.response.WordClassResponse;
import com.website.military.domain.dto.word.request.ExistWordDto;
import com.website.military.domain.dto.word.request.UpdateMeaningDto;
import com.website.military.domain.dto.word.response.DeleteWordResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.domain.dto.word.response.GptWordResponseDto;
import com.website.military.domain.dto.word.response.UpdateMeaningResponseDto;
import com.website.military.repository.GptWordRepository;
import com.website.military.repository.WordRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class WordService {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private GptWordRepository gptWordRepository;

    @Autowired
    private AuthService authService;

    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.url}")
    private String apiUrl;

    @Value("${gemini.api_key}")
    private String apiKey;
    
    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    public ResponseEntity<?> existWord(ExistWordDto dto, HttpServletRequest request){
        String word = dto.getWord();
        Long userId = authService.getUserId(request);
        Optional<Word> existingWord = wordRepository.findByWordAndUser_UserId(word, userId);
        if(existingWord.isPresent()){ // 기존 단어 존재하는 지 체크
            Word words = existingWord.get();
            ExistWordResponseDto response = new ExistWordResponseDto(words.getWordId(), words.getWord(), words.getNoun(),
            words.getVerb(), words.getAdjective(), words.getAdverb(), false);
             return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));      
        }else{
            Optional<GptWord> existingGptWord = gptWordRepository.findByWord(word);  // gpt는 단어만 체크해서 다른사람이 결과를 중복해서 얻어도 같이 나오게 만들기.
            if(existingGptWord.isPresent()){ // Gpt 단어 존재하는 지 체크
                    GptWord words = existingGptWord.get();
                    ExistWordResponseDto response = new ExistWordResponseDto(words.getGptWordId(), words.getWord(), words.getNoun(),
                    words.getVerb(), words.getAdjective(), words.getAdverb(), true);
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));                
            }else{ // 없으니까, gpt 돌려서 단어 만들어서 주기.
                try {
                    String s = getAIDescription(word);
                    String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
                    JSONObject object = new JSONObject(cleanJson);
                    JSONObject newObject = object.getJSONObject(word);
                    List<String> nounList = new ArrayList<>();
                    List<String> verbList = new ArrayList<>();
                    List<String> adjectiveList = new ArrayList<>();
                    List<String> adverbList = new ArrayList<>();
                    JSONArray noun = newObject.getJSONArray("noun");
                    JSONArray verb = newObject.getJSONArray("verb");
                    JSONArray adjective = newObject.getJSONArray("adjective");
                    JSONArray adverb = newObject.getJSONArray("adverb");
                    int nounLength = noun.getString(0) == "" ? 0 : noun.length();
                    int verbLength = verb.getString(0) == "" ? 0 : verb.length();
                    int adjectiveLength = adjective.getString(0) == "" ? 0 : adjective.length();
                    int adverbLength = adverb.getString(0) == "" ? 0 : adverb.length();
                    for(int i=0;i<nounLength;i++){
                        nounList.add(noun.getString(i));    
                    }

                    for(int i=0;i<verbLength;i++){
                        verbList.add(verb.getString(i));    
                    }

                    for(int i=0;i<adjectiveLength;i++){
                        adjectiveList.add(adjective.getString(i));    
                    }
                    for(int i=0;i<adverbLength;i++){
                        adverbList.add(adverb.getString(i));    
                    }
                    if(nounList.isEmpty() && verbList.isEmpty() && adjectiveList.isEmpty() && adverbList.isEmpty()){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청"));
                    }
                    try {
                        GptWord gptWord = new GptWord(word, nounList, verbList, adjectiveList, adverbList);
                        gptWordRepository.save(gptWord);
                        GptWordResponseDto response = new GptWordResponseDto(gptWord.getGptWordId(), gptWord.getWord(),nounList, verbList, adjectiveList, adverbList,true);
                        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDataDto.set("CREATE", response)); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ResponseMessageDto.set(internalError, "서버 에러"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessageDto.set(internalError, "서버 에러"));
                }
            }
        }
    }
    
    // 정확도가 조금 떨어지긴 함. (02. 05.)
    public ResponseEntity<?> correctSpelling(String word){
        String returnWord = correctWordCheck(word);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", returnWord));
    }

    // QA 테스트 미흡 ( 2.3.) -> 수정하면 시간 변경하도록. -> (2. 5) 시간 변경 완료 -> (3.3) 유저가 쓴거만 고칠 수 있게 변경
    public ResponseEntity<?> updateMeaning(Long id, UpdateMeaningDto dto,HttpServletRequest request){ // word(유저가 쓴)가 만든거 변경
        Long userId = authService.getUserId(request);
        String word = dto.getWord();
        Optional<Word> existingWord = wordRepository.findByWordIdAndUser_UserId(id, userId);
        List<String> noun = new ArrayList<>();
        List<String> verb = new ArrayList<>();
        List<String> adjective = new ArrayList<>();
        List<String> adverb = new ArrayList<>();
        if(existingWord.isPresent()){
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
            Word words = existingWord.get();
            if(word.equals(words.getWord())){
            Optional.ofNullable(noun)
            .ifPresent(words::setNoun);
            Optional.ofNullable(verb)
            .ifPresent(words::setVerb);
            Optional.ofNullable(adjective)
            .ifPresent(words::setAdjective);
            Optional.ofNullable(adverb)
            .ifPresent(words::setAdverb);
            words.setUpdatedAt(Instant.now());
            wordRepository.save(words);
            UpdateMeaningResponseDto response = new UpdateMeaningResponseDto(words.getWordId(), noun, verb,
            adjective, adverb);           
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "잘못된 접근입니다."));
    }

    //  유저가 만든 단어 삭제하기. -> 로직 변경
    public ResponseEntity<?> deleteWord(Long id, HttpServletRequest request, boolean isGpt){
        Long userId = authService.getUserId(request);
        if(isGpt){
            Optional<Word> existingWord = wordRepository.findByWordIdAndUser_UserId(id, userId);
            if(existingWord.isPresent()){
                Word words = existingWord.get();
                wordRepository.deleteById(id);
                DeleteWordResponseDto response = new DeleteWordResponseDto(id, words.getWord(), words.getNoun(), words.getVerb(),
                words.getAdjective(), words.getAdverb());
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessageDto.set(unAuthorize, "잘못된 접근입니다."));
            }
        }else{
            Optional<GptWord> existingWord = gptWordRepository.findById(id);
            if(existingWord.isPresent()){
                GptWord words = existingWord.get();
                gptWordRepository.deleteById(id);
                DeleteWordResponseDto response = new DeleteWordResponseDto(id, words.getWord(), words.getNoun(), words.getVerb(),
                words.getAdjective(), words.getAdverb());
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            } 
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }
    

    public String getAIDescription(String word){
        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = new GeminiRequestDto();
 // 형식 다시 조금 수정하기.
        String processedSentence = word + " 뜻을 json형식으로 보여줘. 품사는 명사, 동사, 형용사, 부사 순서로 알려주면 돼. 뜻이 없으면 빈칸으로 만들어줘. 각각의 개수는 2~3개로 해줘."
        + "예시 출력(word가 'sleep'일 경우):\n" +
        "{\n" +
        "  \"sleep\": {\n" +
        "    \"adjective\": [\"\"],\n" +
        "    \"verb\": [\"자다\", \"잠자다\", \"수면을 취하다\"],\n" +
        "    \"noun\": [\"수면\", \"잠\", \"휴식\"],\n" +
        "    \"adverb\": [\"\"]\n" +
        "  }\n" +
        "}";
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
        String processedSentence = word + " 의 단어 뜻이 있으면" + word + "그대로 대답하고 없으면, 단어의 뜻이 아닌 형태가 가장 비슷한 단어를 영단어 하나만 대답해줘." + 
        "다른 첨언은 없어도 돼. 단어만 얘기해줘." +
        "예시출력(word가 'word'일경우) : word";
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
