package com.website.military.service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.military.domain.Entity.GptWord;
import com.website.military.domain.Entity.GptWordSetMapping;
import com.website.military.domain.Entity.TestProblems;
import com.website.military.domain.Entity.Tests;
import com.website.military.domain.Entity.Word;
import com.website.military.domain.Entity.WordSetMapping;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.gemini.request.GeminiRequestDto;
import com.website.military.domain.dto.gemini.response.GeminiResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.test.request.QuestionRequest;
import com.website.military.domain.dto.test.response.DeleteExamResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponseDto;
import com.website.military.repository.TestProblemsRepository;
import com.website.military.repository.TestsRepository;
import com.website.military.repository.WordSetsRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TestService {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private WordSetsRepository wordSetsRepository;

    @Autowired
    private TestProblemsRepository testProblemsRepository;

    @Autowired
    private TestsRepository testsRepository;

    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.url}")
    private String apiUrl;

    @Value("${gemini.api_key}")
    private String apiKey;


    public ResponseEntity<?> generateExamList(HttpServletRequest request, Long setId){
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if(existingWordSets.isPresent()){
            WordSets wordSets = existingWordSets.get();
            Tests tests = new Tests(wordSets.getUser(), wordSets, 0);
            testsRepository.save(tests); // test 생성
            List<WordSetMapping> mapping = wordSets.getWordsetmapping();
            List<GptWordSetMapping> gptmapping = wordSets.getGptwordsetMappings();
            int length = mapping.size() + gptmapping.size();

            if(length < 20){
                Collections.shuffle(mapping); // 매핑 된거 먼저 셔플을 해야 넣은 순서대로 문제가 나오지 않음.
                Collections.shuffle(gptmapping);
                List<GenerateExamListResponseDto> responseDtos = new ArrayList<>();
                Long problemNumber = 1L;             // 문제를 response로 낼 때는 몇번이지 알려줘야하기에.
                for(WordSetMapping tmp : mapping){
                    Word tmpWord = tmp.getWord();
                    GenerateExamListResponseDto responseDto = parsingData(tmpWord, problemNumber);
                    Collections.shuffle(responseDto.getList());
                    List<QuestionRequest> words = new ArrayList<>();
                    for(GenerateExamListResponse list : responseDto.getList()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();   
                        String jsonString = objectMapper.writeValueAsString(words);
                        String answer = responseDto.getAnswer();
                        TestProblems testProblems = new TestProblems(tests, jsonString, problemNumber, answer);
                        testProblemsRepository.save(testProblems);
                        problemNumber = problemNumber + 1;   
                        responseDtos.add(responseDto); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }  
                }

                for(GptWordSetMapping tmp : gptmapping){
                    GptWord tmpWord = tmp.getGptword();
                    GenerateExamListResponseDto responseDto = parsingData(tmpWord, problemNumber);
                    Collections.shuffle(responseDto.getList());
                    List<QuestionRequest> words = new ArrayList<>();
                    for(GenerateExamListResponse list : responseDto.getList()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();   
                        String jsonString = objectMapper.writeValueAsString(words);  
                        String answer = responseDto.getAnswer();
                        TestProblems testProblems = new TestProblems(tests, jsonString, problemNumber, answer);
                        testProblemsRepository.save(testProblems);
                        problemNumber = problemNumber + 1;   
                        responseDtos.add(responseDto); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }  

                }
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", responseDtos));
            }else{
                // 20개 넘으면 20개를 고르는 알고리즘 존재.
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
    }

    public ResponseEntity<?> deleteTest(HttpServletRequest request, Long testId){
        Long userId = authService.getUserId(request);
        Optional<Tests> existingWordSets = testsRepository.findByUser_UserIdAndTestId(userId, testId);
        if(existingWordSets.isPresent()){
            Tests tests = existingWordSets.get();
            DeleteExamResponse response = new DeleteExamResponse(testId, tests.getWordsets().getSetName(), Instant.now());
            testsRepository.deleteById(testId);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
    }

    // AI 이용해서 문제 만들어내는 메서드
    public String getAIDescription(String word, String meaning){
        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = new GeminiRequestDto();
        String processedSentence = word + "에 대한 의미를 묻는 객관식 문제를 만들어줘." + word + "의 의미를 " + meaning + "를 정답으로 해서 만들어줘. " +
        "다른 보기는 " + word + "와는 전혀 관련이 없는 단어의 뜻으로 만들어줘. " +
        "선택지의 품사는 '" + meaning + "'와 똑같이 맞춰줘. " +
        "JSON 형식으로 다음과 같이 만들어줘."+
        "{\"question\":\"객관식 문제의 질문 내용\", " +
        "\"answer\":\"정답 선택지의 ID 값\", " +
        "\"options\":[{\"id\":\"A\",\"text\":\"보기1\"}, " +
        "{\"id\":\"B\",\"text\":\"보기2\"}, " +
        "{\"id\":\"C\",\"text\":\"보기3\"}, " +
        "{\"id\":\"D\",\"text\":\"보기4\"}]}";
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

    // 유저가 만든 단어 품사 골라주는 메서드 
    private Long wordPOS(Word word) {
        return getRandomPOS(word.getNoun(), word.getVerb(), word.getAdjective(), word.getAdverb());
    }
    
    // gpt가 만든 단어 품사 골라주는 메서드 
    private Long wordPOS(GptWord word) {
        return getRandomPOS(word.getNoun(), word.getVerb(), word.getAdjective(), word.getAdverb());
    }
    
    // 품사 랜덤으로 고르는 메서드
    private Long getRandomPOS(List<String> noun, List<String> verb, List<String> adjective, List<String> adverb) {
        List<Long> indexList = new ArrayList<>();
    
        addPOSIndex(indexList, noun, 0L);
        addPOSIndex(indexList, verb, 1L);
        addPOSIndex(indexList, adjective, 2L);
        addPOSIndex(indexList, adverb, 3L);
    
        if (indexList.isEmpty()) {
            throw new IllegalStateException("단어에 품사가 없습니다.");
        }
    
        Collections.shuffle(indexList);
        return indexList.get(0);
    }
    
    // 리스트가 비어있지 않으면 인덱스를 추가
    private void addPOSIndex(List<Long> indexList, List<String> posList, Long index) {
        if (!posList.isEmpty() && !posList.get(0).equals("")) {
            indexList.add(index);
        }
    }

    // gpt가 쓴 단어 단어를 골라주는 메서드 -> 아직 모두 가져오는건 못함. get(0)만 가능.
    private GenerateExamListResponseDto parsingData(GptWord word, Long problemNumber){
        List<GenerateExamListResponse> responses = new ArrayList<>();
        Long id = wordPOS(word);
        String meaning = "";
        switch (id.intValue()) {
            case 0:
                meaning = word.getNoun().get(0);
                break;
            case 1:
                meaning = word.getVerb().get(0);
                break;
            case 2:
                meaning = word.getAdjective().get(0);
                break;
            case 3:
                meaning = word.getAdverb().get(0);
                break;
        }
        String s = getAIDescription(word.getWord(), meaning);
        String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
        JSONObject object = new JSONObject(cleanJson);
        JSONArray newObject = object.getJSONArray("options"); // options에 나오는거까지 찍힘.
        String question = object.getString("question");
        String answer = object.getString("answer");
        for(Object obj : newObject){
            JSONObject jsonObj = (JSONObject) obj;
            GenerateExamListResponse response = new GenerateExamListResponse(jsonObj.getString("id"), jsonObj.getString("text"));
            responses.add(response);
        }
        GenerateExamListResponseDto responseDto = new GenerateExamListResponseDto(problemNumber, responses, question, answer);
        return responseDto;
    }

    // 유저가 쓴 단어 단어를 골라주는 메서드
    private GenerateExamListResponseDto parsingData(Word word, Long problemNumber){
        List<GenerateExamListResponse> responses = new ArrayList<>();
        Long id = wordPOS(word);
        String meaning = "";
        switch (id.intValue()) {
            case 0:
                meaning = word.getNoun().get(0);
                break;
            case 1:
                meaning = word.getVerb().get(0);
                break;
            case 2:
                meaning = word.getAdjective().get(0);
                break;
            case 3:
                meaning = word.getAdverb().get(0);
                break;
        }
        String s = getAIDescription(word.getWord(), meaning);
        String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
        JSONObject object = new JSONObject(cleanJson);
        JSONArray newObject = object.getJSONArray("options"); // options에 나오는거까지 찍힘.
        String question = object.getString("question");
        String answer = object.getString("answer");
        for(Object obj : newObject){
            JSONObject jsonObj = (JSONObject) obj;
            GenerateExamListResponse response = new GenerateExamListResponse(jsonObj.getString("id"), jsonObj.getString("text"));
            responses.add(response);
        }
        GenerateExamListResponseDto responseDto = new GenerateExamListResponseDto(problemNumber, responses, question, answer);
        return responseDto;
    }


}


