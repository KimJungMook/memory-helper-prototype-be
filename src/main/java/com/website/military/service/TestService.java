package com.website.military.service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
import com.website.military.config.jwt.JwtAccessDeniedHandler;
import com.website.military.domain.Entity.GptWord;
import com.website.military.domain.Entity.GptWordSetMapping;
import com.website.military.domain.Entity.Mistakes;
import com.website.military.domain.Entity.Results;
import com.website.military.domain.Entity.SolvedProblems;
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
import com.website.military.domain.dto.test.response.CheckListResponse;
import com.website.military.domain.dto.test.response.CheckResponse;
import com.website.military.domain.dto.test.response.DeleteExamResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponseDto;
import com.website.military.domain.dto.test.response.GenerateExamListTestIdResponse;
import com.website.military.domain.dto.test.response.GetAllExamListResponse;
import com.website.military.domain.dto.test.response.GetTestProblemsResponse;
import com.website.military.repository.MistakesRepository;
import com.website.military.repository.ResultsRepository;
import com.website.military.repository.SolvedProblemRepository;
import com.website.military.repository.TestProblemsRepository;
import com.website.military.repository.TestsRepository;
import com.website.military.repository.WordSetsRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TestService {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private WordSetsRepository wordSetsRepository;

    @Autowired
    private TestProblemsRepository testProblemsRepository;

    @Autowired
    private TestsRepository testsRepository;

    @Autowired
    private SolvedProblemRepository solvedProblemRepository;

    @Autowired
    private MistakesRepository mistakesRepository;

    @Autowired
    private ResultsRepository resultsRepository;

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

    TestService(JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    public ResponseEntity<?> getAllExamList(HttpServletRequest request){ // 시험이 뭐뭐 있었는지를 체크하는 리스트
        Long userId = authService.getUserId(request);
        List<Tests> testsList = testsRepository.findByUser_UserId(userId);
        List<GetAllExamListResponse> responses = new ArrayList<>();
        if(testsList.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }else{
            for(Tests test : testsList){
                GetAllExamListResponse response = new GetAllExamListResponse(test.getTestId(), test.getCreatedAt(), test.getTestType());
                responses.add(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",responses));
        }
    }

    public ResponseEntity<?> getTestProblems(HttpServletRequest request, Long id){
        Long userId = authService.getUserId(request);
        Optional<Tests> existingTests = testsRepository.findByUser_UserIdAndTestId(userId, id);
        if(existingTests.isPresent()){
            List<TestProblems> problems = existingTests.get().getTestproblems();
            List<GetTestProblemsResponse> responses = new ArrayList<>();
            for(TestProblems problem : problems){
                List<QuestionRequest> multipleChoiceList = parseMultipleChoice(problem.getMultipleChoice());
                GetTestProblemsResponse response = new GetTestProblemsResponse(problem.getProblemId(), problem.getProblemNumber(), multipleChoiceList,
                problem.getQuestion(), problem.getAnswer());
                responses.add(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",responses));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

    // 시험 문제 만드는 메서드 
    public ResponseEntity<?> generateExamList(HttpServletRequest request, Long setId){
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if(existingWordSets.isPresent()){
            WordSets wordSets = existingWordSets.get();
            List<WordSetMapping> mapping = wordSets.getWordsetmapping();
            List<GptWordSetMapping> gptmapping = wordSets.getGptwordsetMappings();
            int mappingSize = mapping.size(); // mapping size 밑에서 쓸일이 있음.
            int gptmappingSize = gptmapping.size();
            int length = wordSets.getWordCount();
            Tests tests = new Tests(wordSets.getUser(), wordSets, 0);

            Collections.shuffle(mapping); // 매핑 된거 먼저 셔플을 해야 넣은 순서대로 문제가 나오지 않음.
            Collections.shuffle(gptmapping);

            if(length < 20){
                tests.setTestCount(length);
                // testsRepository.save(tests); // test 생성
                List<GenerateExamListResponseDto> responseDtos = new ArrayList<>();
                Long problemNumber = 1L;
                List<Word> wordList = new ArrayList<>();
                List<GptWord> gptWordList = new ArrayList<>();
                for(WordSetMapping tmp : mapping){
                    Word tmpWord = tmp.getWord();
                    wordList.add(tmpWord);
                }
                List<GenerateExamListResponseDto> responses = parsingData(wordList);

                for(GenerateExamListResponseDto response : responses){
                    List<QuestionRequest> words = new ArrayList<>();
                    for(GenerateExamListResponse list : response.getMultipleChoice()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();   
                        String question = response.getQuestion();
                        String jsonString = objectMapper.writeValueAsString(words);
                        char answer = response.getAnswer().charAt(0);
                        TestProblems testProblems = new TestProblems(tests, jsonString, question, response.getProblemNumber(), answer);
                        problemNumber = response.getProblemNumber();
                        // testProblemsRepository.save(testProblems); 
                        responseDtos.add(response); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }  
                    
                }

                for(GptWordSetMapping tmp : gptmapping){
                    GptWord tmpWord = tmp.getGptword();
                    gptWordList.add(tmpWord);
                }
                List<GenerateExamListResponseDto> gptResponses = parsingGptData(gptWordList, problemNumber);
                for(GenerateExamListResponseDto response : gptResponses){
                    List<QuestionRequest> words = new ArrayList<>();
                    for(GenerateExamListResponse list : response.getMultipleChoice()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();   
                        String question = response.getQuestion();
                        String jsonString = objectMapper.writeValueAsString(words);
                        char answer = response.getAnswer().charAt(0);
                        TestProblems testProblems = new TestProblems(tests, jsonString, question, response.getProblemNumber(), answer);
                        // testProblemsRepository.save(testProblems); 
                        responseDtos.add(response); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }  
                    
                }

                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", responseDtos));
                // 2025.04.03 list형태로 만들어내는것 성공 -> 10개단위로 끊어내는 것도 만들어야함. -> 아직 테스트에 넣는 것까지는 불가능.
                // GenerateExamListTestIdResponse response = new GenerateExamListTestIdResponse(tests.getTestId(), responseDtos);
                // return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }else{
                int minValue = Math.min(gptmappingSize, mappingSize);
                // 5 17 -> 5 15 4 16  3 17 
                // 6 17 -> 6 14 5 15 4 16 3 17
                // 14 16 -> 14 6 13 7 12 8 .... 4 16
                // 9 12 -> 9 1 8 2 .... 0 10
                // 12 12 -> 10 0 9 1  
                minValue = (minValue > 20) ? 20 : minValue;
                int variety = length + 1  - 20;
                variety = (variety > 20) ? 20 : variety;
                int randomNumber = ThreadLocalRandom.current().nextInt(0, variety); // 0부터 size-1까지
                int result = (randomNumber > 20) ? 20 : (minValue - randomNumber);
                List<WordSetMapping> randomSelection = (minValue == mappingSize) ? mapping.subList(0, result) : mapping.subList(0, 20-result);
                List<GptWordSetMapping> gptRandomSelection = (minValue == gptmappingSize) ? gptmapping.subList(0, result) : gptmapping.subList(0, 20-result);
                // 이후부터는 다시 하기
                tests.setTestCount(20);
                testsRepository.save(tests); // test 생성

                List<GenerateExamListResponseDto> responseDtos = new ArrayList<>();
                Long problemNumber = 1L;             // 문제를 response로 낼 때는 몇번이지 알려줘야하기에.
                for(WordSetMapping tmp : randomSelection){
                    Word tmpWord = tmp.getWord();
                    
                    GenerateExamListResponseDto responseDto = null;
                    try {
                        Thread.sleep(1000);  // 1초 대기
                        // responseDto = parsingData(tmpWord, problemNumber);   
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }
                    Collections.shuffle(responseDto.getMultipleChoice());
                    List<QuestionRequest> words = new ArrayList<>();
                    for(GenerateExamListResponse list : responseDto.getMultipleChoice()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();   
                        String question = responseDto.getQuestion();
                        String jsonString = objectMapper.writeValueAsString(words);
                        char answer = responseDto.getAnswer().charAt(0);
                        TestProblems testProblems = new TestProblems(tests, jsonString, question, problemNumber, answer);
                        testProblemsRepository.save(testProblems);
                        problemNumber = problemNumber + 1;   
                        responseDtos.add(responseDto); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }  
                }

                for(GptWordSetMapping tmp : gptRandomSelection){
                    GptWord tmpWord = tmp.getGptword();
                    
                    GenerateExamListResponseDto responseDto = null;
                    try {
                        // responseDto = parsingData(tmpWord, problemNumber);   
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }

                    Collections.shuffle(responseDto.getMultipleChoice());
                    List<QuestionRequest> words = new ArrayList<>();
                    for(GenerateExamListResponse list : responseDto.getMultipleChoice()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();  
                        String question = responseDto.getQuestion();
                        String jsonString = objectMapper.writeValueAsString(words);  
                        char answer = responseDto.getAnswer().charAt(0);
                        TestProblems testProblems = new TestProblems(tests, jsonString ,question, problemNumber, answer);
                        testProblemsRepository.save(testProblems);
                        problemNumber = problemNumber + 1;   
                        responseDtos.add(responseDto); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }  
                    
                }
                GenerateExamListTestIdResponse response = new GenerateExamListTestIdResponse(tests.getTestId(), responseDtos);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
    }


    public ResponseEntity<?> checkAnswers(HttpServletRequest request, Long testId, List<Character> checkedAnswers){
        Long userId = authService.getUserId(request);
        Optional<Tests> existingTests = testsRepository.findByUser_UserIdAndTestId(userId, testId);
        CheckListResponse responses = new CheckListResponse();
        List<CheckResponse> correctList = new ArrayList<>();
        List<SolvedProblems> solvedProblemsList = new ArrayList<>();
        List<Mistakes> mistakesList = new ArrayList<>();
        List<CheckResponse> incorrectList = new ArrayList<>();
        if(existingTests.isPresent()){
            Tests tests = existingTests.get();
            List<TestProblems> problems = tests.getTestproblems();
            int index = 0;
            int mistakeIndex = 0;
            for(TestProblems problem : problems){
                List<QuestionRequest> multipleChoiceList = parseMultipleChoice(problem.getMultipleChoice());
                if(checkedAnswers.get(index).equals(problem.getAnswer())){
                    SolvedProblems solvedProblems = new SolvedProblems(problem);
                    solvedProblemRepository.save(solvedProblems);
                    solvedProblemsList.add(solvedProblems);
                    CheckResponse response = new CheckResponse(problem.getProblemNumber(), multipleChoiceList, problem.getAnswer()); 
                    correctList.add(response);
                }else{
                    Mistakes mistakesProblems = new Mistakes(problem);
                    mistakesRepository.save(mistakesProblems);
                    mistakesList.add(mistakesProblems);
                    CheckResponse response = new CheckResponse(problem.getProblemNumber(), multipleChoiceList, problem.getAnswer()); 
                    incorrectList.add(response);
                    mistakeIndex++;
                }
                index++; // Result에 저장하기. 채점을 했으니까. 여기서부터 다시하기. 25.03.08 (23:23)
            }
            Results results = new Results(tests.getUser(), tests, (index-mistakeIndex)*100/index, mistakesList, solvedProblemsList);

            for (Mistakes mistake : mistakesList) {
                mistake.setResults(results);
            }
            for (SolvedProblems solvedProblem : solvedProblemsList) {
                solvedProblem.setResults(results);
            }

            resultsRepository.save(results);
            // result 만들어내기 03.11 (23:33) -> result 저장후, mistake solvedproblem 연결 완료 -> QA 부족 체크하기.
            responses.setCorrectList(correctList);
            responses.setIncorrectList(incorrectList);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", responses));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));


    }

    // 시험 연결된거 삭제하기. 
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

    public String getAIDescription(List<String> wordList, List<String> meaningList){
        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = new GeminiRequestDto();
        StringBuilder processedSentence = new StringBuilder();
        for(int i = 0;i<wordList.size();i++){
            String[] choices = {"A", "B", "C", "D"};
            String correctAnswer = choices[new Random().nextInt(choices.length)]; // 랜덤 선택
            processedSentence.append(wordList.get(i)).append("에 대한 의미를 묻는 객관식 문제를 만들어줘. ")
            .append(wordList.get(i)).append("의 의미를 '").append(meaningList.get(i)).append("'로 하고, 이를 정답으로 설정해줘. ")
            .append("나머지 보기는 ").append(wordList.get(i)).append("와 관련 없는 단어의 뜻으로 만들어줘. ")
            .append("선택지의 품사는 '").append(meaningList.get(i)).append("'와 동일하게 맞춰줘. ")
            .append("정답 선택지의 ID 값은 ").append(correctAnswer).append("로 설정해줘. ");
        }
        processedSentence.append("JSON 형식으로 다음과 같이 만들어줘: ")
        .append("{\"question\":\"객관식 문제의 질문 내용\", ")
        .append("\"answer\":\"").append('A').append("\", ")
        .append("\"options\":[")
        .append("{\"id\":\"A\",\"text\":\"보기1\"}, ")
        .append("{\"id\":\"B\",\"text\":\"보기2\"}, ")
        .append("{\"id\":\"C\",\"text\":\"보기3\"}, ")
        .append("{\"id\":\"D\",\"text\":\"보기4\"}")
        .append("]}")
        .append("마지막에는 배열로 만들어줘.");
        request.createGeminiReqDto(processedSentence.toString());
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

    private List<GenerateExamListResponseDto> parsingData(List<Word> words){
        Long problemNumber = 1L;             // 문제를 response로 낼 때는 몇번이지 알려줘야하기에.
        List<String> meaningList = new ArrayList<>();
        List<String> wordList = new ArrayList<>(); 
        for(Word word : words){
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
            meaningList.add(meaning);
            wordList.add(word.getWord());
        }
        String s = getAIDescription(wordList, meaningList);
        String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
        JSONArray objectArray = new JSONArray(cleanJson);
        List<GenerateExamListResponseDto> responseDtos = new ArrayList<>();
        for(int i=0;i<objectArray.length();i++){
            List<GenerateExamListResponse> responses = new ArrayList<>();
            JSONArray newObject = objectArray.getJSONObject(i).getJSONArray("options"); // options에 나오는거까지 찍힘.
            String question = objectArray.getJSONObject(i).getString("question");
            String answer = objectArray.getJSONObject(i).getString("answer");
            for(Object obj : newObject){
                JSONObject jsonObj = (JSONObject) obj;
                GenerateExamListResponse response = new GenerateExamListResponse(jsonObj.getString("id"), jsonObj.getString("text"));
                responses.add(response);
            }
            GenerateExamListResponseDto responseDto = new GenerateExamListResponseDto(problemNumber, responses, question, answer);    
            problemNumber = problemNumber + 1;
            Collections.shuffle(responseDto.getMultipleChoice());     
            responseDtos.add(responseDto);   
        }
        return responseDtos;
    }

    private List<GenerateExamListResponseDto> parsingGptData(List<GptWord> words, Long problemNumber){
        Long number = problemNumber + 1;             // 문제를 response로 낼 때는 몇번이지 알려줘야하기에.
        List<String> meaningList = new ArrayList<>();
        List<String> wordList = new ArrayList<>(); 
        for(GptWord word : words){
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
            meaningList.add(meaning);
            wordList.add(word.getWord());
        }
        String s = getAIDescription(wordList, meaningList);
        String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
        JSONArray objectArray = new JSONArray(cleanJson);
        List<GenerateExamListResponseDto> responseDtos = new ArrayList<>();
        for(int i=0;i<objectArray.length();i++){
            List<GenerateExamListResponse> responses = new ArrayList<>();
            JSONArray newObject = objectArray.getJSONObject(i).getJSONArray("options"); // options에 나오는거까지 찍힘.
            String question = objectArray.getJSONObject(i).getString("question");
            String answer = objectArray.getJSONObject(i).getString("answer");
            for(Object obj : newObject){
                JSONObject jsonObj = (JSONObject) obj;
                GenerateExamListResponse response = new GenerateExamListResponse(jsonObj.getString("id"), jsonObj.getString("text"));
                responses.add(response);
            }
            GenerateExamListResponseDto responseDto = new GenerateExamListResponseDto(number, responses, question, answer);    
            number = number + 1;
            Collections.shuffle(responseDto.getMultipleChoice());     
            responseDtos.add(responseDto);   
        }
        return responseDtos;
    }

    public List<QuestionRequest> parseMultipleChoice(String multipleChoiceJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(multipleChoiceJson, objectMapper.getTypeFactory().constructCollectionType(List.class, QuestionRequest.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


// lion에 대한 의미를 묻는 객관식 문제를 만들어줘. 
// lion의 의미를 '사자'로 하고, 이를 정답으로 설정해줘. 
// 나머지 보기는 lion와 관련 없는 단어의 뜻으로 만들어줘. 
// 선택지의 품사는 '사자'와 동일하게 맞춰줘. 
// 정답 선택지의 ID 값은 A로 설정해줘. 
// key에 대한 의미를 묻는 객관식 문제를 만들어줘.  
// lion의 의미를 '열쇠'로 하고, 이를 정답으로 설정해줘.  
// 나머지 보기는 key와 관련 없는 단어의 뜻으로 만들어줘.  
// 선택지의 품사는 '키'와 동일하게 맞춰줘.  
// 정답 선택지의 ID 값은 A로 설정해줘.  
// JSON 형식으로 다음과 같이 만들어줘: 
// {"question":"객관식 문제의 질문 내용", 
//  "answer":"A", 
//  "options":[{"id":"A","text":"보기1"}, 
//  {"id":"B","text":"보기2"}, 
//  {"id":"C","text":"보기3"}, 
//  {"id":"D","text":"보기4"}]}

// 응답 결과.

// ```json
// [
//   {
//     "question": "lucky의 의미는 무엇입니까?",
//     "answer": "D",
//     "options": [
//       {"id": "A", "text": "슬픈"},
//       {"id": "B", "text": "화난"},
//       {"id": "C", "text": "행복한"},
//       {"id": "D", "text": "운이 좋은"}
//     ]
//   },
//   {
//     "question": "word의 의미는 무엇입니까?",
//     "answer": "A",
//     "options": [
//       {"id": "A", "text": "단어"},
//       {"id": "B", "text": "문장"},
//       {"id": "C", "text": "구절"},
//       {"id": "D", "text": "글"}
//     ]
//   },
//   {
//     "question": "drink의 의미는 무엇입니까?",
//     "answer": "B",
//     "options": [
//       {"id": "A", "text": "먹다"},
//       {"id": "B", "text": "마시다"},
//       {"id": "C", "text": "씹다"},
//       {"id": "D", "text": "맛보다"}
//     ]
//   }
// ]
// ```
