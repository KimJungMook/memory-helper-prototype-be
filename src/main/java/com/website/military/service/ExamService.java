package com.website.military.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.military.domain.Entity.Mistakes;
import com.website.military.domain.Entity.Results;
import com.website.military.domain.Entity.SolvedProblems;
import com.website.military.domain.Entity.Problems;
import com.website.military.domain.Entity.Exam;
import com.website.military.domain.Entity.Word;
import com.website.military.domain.Entity.WordSetMapping;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.exam.request.QuestionRequest;
import com.website.military.domain.dto.exam.response.CheckListResponse;
import com.website.military.domain.dto.exam.response.CheckResponse;
import com.website.military.domain.dto.exam.response.DeleteExamResponse;
import com.website.military.domain.dto.exam.response.GenerateExamListResponse;
import com.website.military.domain.dto.exam.response.GenerateExamListTestIdResponse;
import com.website.military.domain.dto.exam.response.GetAllExamListResponse;
import com.website.military.domain.dto.exam.response.GetTestProblemsResponse;
import com.website.military.domain.dto.gemini.request.GeminiRequestDto;
import com.website.military.domain.dto.gemini.response.GeminiResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.repository.MistakesRepository;
import com.website.military.repository.ResultsRepository;
import com.website.military.repository.SolvedProblemRepository;
import com.website.military.repository.ProblemsRepository;
import com.website.military.repository.ExamRepository;
import com.website.military.repository.WordSetsRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ExamService { 
    @Autowired
    private AuthService authService;
    
    @Autowired
    private WordSetsRepository wordSetsRepository;

    @Autowired
    private ProblemsRepository problemsRepository;

    @Autowired
    private ExamRepository examRepository;

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

    public ResponseEntity<?> getAllExamList(HttpServletRequest request){ // 시험이 뭐뭐 있었는지를 체크하는 리스트
        Long userId = authService.getUserId(request);
        List<Exam> testsList = examRepository.findByUser_UserId(userId);
        List<GetAllExamListResponse> responses = new ArrayList<>();
        if(testsList.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }else{
            for(Exam test : testsList){
                GetAllExamListResponse response = new GetAllExamListResponse(test.getExamId(), test.getCreatedAt(), test.getTestType(), test.getTestCount());
                responses.add(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",responses));
        }
    }

    public ResponseEntity<?> getTestProblems(HttpServletRequest request, Long examId){
        Long userId = authService.getUserId(request);
        Optional<Exam> existingTests = examRepository.findByUser_UserIdAndExamId(userId, examId);
        if(existingTests.isPresent()){
            List<Problems> problems = existingTests.get().getProblems();
            List<GetTestProblemsResponse> responses = new ArrayList<>();
            for(Problems problem : problems){
                List<QuestionRequest> multipleChoiceList = parseMultipleChoice(problem.getMultipleChoice());
                GetTestProblemsResponse response = new GetTestProblemsResponse(problem.getProblemId(), problem.getProblemNumber(), problem.getQuestion(), 
                multipleChoiceList);
                responses.add(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",responses));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

    // 시험 문제 만드는 메서드  -> 중복으로 생성되는거 일단 막기. -> 이상한 거 ai로 생기는거 막기. (2025.05.01)
    @Transactional
    public ResponseEntity<?> generateExamList(HttpServletRequest request, Long setId){
        Long userId = authService.getUserId(request);
        Optional<WordSets> existingWordSets = wordSetsRepository.findByUser_UserIdAndSetId(userId, setId);
        if(existingWordSets.isPresent()){
            WordSets wordSets = existingWordSets.get();
            List<WordSetMapping> mapping = wordSets.getWordsetmapping();
            int length = wordSets.getWordCount();
            Exam tests = new Exam(wordSets.getUser(), wordSets, 0);
            Collections.shuffle(mapping); // 매핑 된거 먼저 셔플을 해야 넣은 순서대로 문제가 나오지 않음.

            if(length < 20){
                tests.setTestCount(length);
                examRepository.save(tests); // test 생성
                List<GenerateExamListResponse> responseDtos = new ArrayList<>();
                List<GenerateExamListResponse> firstTenResponses = new ArrayList<>();
                List<GenerateExamListResponse> lastResponses = new ArrayList<>();
                List<Word> wordList = new ArrayList<>();
                for(WordSetMapping tmp : mapping){
                    Word tmpWord = tmp.getWord();
                    wordList.add(tmpWord);
                }

                if(wordList.size() > 10){
                    firstTenResponses = parsingData(wordList.subList(0, 10), 1L);
                    lastResponses = parsingData(wordList.subList(10, wordList.size()), 11L);
                    firstTenResponses.addAll(lastResponses);
                }else{
                    firstTenResponses = parsingData(wordList, 1L);
                }

                for(GenerateExamListResponse response : firstTenResponses){
                    List<QuestionRequest> words = new ArrayList<>();
                    for(QuestionRequest list : response.getMultipleChoice()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();   
                        String question = response.getQuestion();
                        String jsonString = objectMapper.writeValueAsString(words);
                        int answer = response.getAnswer();
                        Problems testProblems = new Problems(tests, jsonString, question, response.getProblemNumber(), answer);
                        problemsRepository.save(testProblems); 
                        responseDtos.add(response); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }
                }
                // 2025.04.03 list형태로 만들어내는것 성공 -> 10개단위로 끊어내는 것도 만들어야함. -> 아직 테스트에 넣는 것까지는 불가능.
                // 2025.04.26 test 만드는 것까지 성공. -> QA 부족. -> 아직까지는 에러는 안뜸
                GenerateExamListTestIdResponse response = new GenerateExamListTestIdResponse(tests.getExamId(), responseDtos);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }else{
                List<GenerateExamListResponse> responseDtos = new ArrayList<>();
                List<GenerateExamListResponse> firstTenResponses = new ArrayList<>();
                List<GenerateExamListResponse> lastResponses = new ArrayList<>();
                List<Word> wordList = new ArrayList<>();
            
                tests.setTestCount(20);
                examRepository.save(tests); // test 생성

                for(WordSetMapping tmp : mapping.subList(0, 20)){
                    Word tmpWord = tmp.getWord();
                    wordList.add(tmpWord);
                }
                firstTenResponses = parsingData(wordList.subList(0, 10), 1L);
                lastResponses = parsingData(wordList.subList(10, 20), 11L);
                firstTenResponses.addAll(lastResponses);

                for(GenerateExamListResponse response : firstTenResponses){
                    List<QuestionRequest> words = new ArrayList<>();
                    for(QuestionRequest list : response.getMultipleChoice()){
                        String id = list.getId();
                        String meaning = list.getMeaning();
                        words.add(new QuestionRequest(id, meaning));
                    }
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();   
                        String question = response.getQuestion();
                        String jsonString = objectMapper.writeValueAsString(words);
                        int answer = response.getAnswer();
                        Problems testProblems = new Problems(tests, jsonString, question, response.getProblemNumber(), answer);
                        problemsRepository.save(testProblems); 
                        responseDtos.add(response); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                    }  
                    
                }

                GenerateExamListTestIdResponse response = new GenerateExamListTestIdResponse(tests.getExamId(), responseDtos);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
    }

    @Transactional
    public ResponseEntity<?> checkAnswers(HttpServletRequest request, Long examId, List<Integer> checkedAnswers){
        Long userId = authService.getUserId(request);
        Optional<Exam> existingTests = examRepository.findByUser_UserIdAndExamId(userId, examId);
        List<CheckResponse> correctList = new ArrayList<>();
        List<SolvedProblems> solvedProblemsList = new ArrayList<>();
        List<Mistakes> mistakesList = new ArrayList<>();
        List<CheckResponse> incorrectList = new ArrayList<>();
        if(existingTests.isPresent() && (checkedAnswers.size() == existingTests.get().getTestCount())){
            Exam exams = existingTests.get();
            if(exams.getResults().isEmpty()){
                List<Problems> problems = exams.getProblems();
                int index = 0;
                int mistakeIndex = 0;
                for(Problems problem : problems){
                    List<QuestionRequest> multipleChoiceList = parseMultipleChoice(problem.getMultipleChoice());
                    if(checkedAnswers.get(index) == problem.getAnswer()){
                        Problems newProblems = new Problems(problem, checkedAnswers.get(index));
                        problemsRepository.save(newProblems);
                        SolvedProblems solvedProblems = new SolvedProblems(problem);
                        solvedProblemsList.add(solvedProblems);
                        CheckResponse response = new CheckResponse(problem.getProblemId(), problem.getProblemNumber(), multipleChoiceList, checkedAnswers.get(index), problem.getAnswer()); 
                        correctList.add(response);
                    }else{
                        Problems newProblems = new Problems(problem, checkedAnswers.get(index));
                        problemsRepository.save(newProblems);
                        Mistakes mistakesProblems = new Mistakes(problem);
                        mistakesList.add(mistakesProblems);
                        CheckResponse response = new CheckResponse(problem.getProblemId(), problem.getProblemNumber(), multipleChoiceList, checkedAnswers.get(index),problem.getAnswer()); 
                        incorrectList.add(response);
                        mistakeIndex++;
                    }
                    index++; // Result에 저장하기. 채점을 했으니까. 여기서부터 다시하기. 25.03.08 (23:23)
                }
                BigDecimal bd = new BigDecimal((double)(index-mistakeIndex)*100/index);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                double roundedScore = bd.doubleValue();
                Results results = new Results(exams.getUser(), exams, roundedScore, mistakesList, solvedProblemsList);

                for (Mistakes mistake : mistakesList) {
                    mistake.setResults(results);
                    mistakesRepository.save(mistake);
                }

                for (SolvedProblems solvedProblem : solvedProblemsList) {
                    solvedProblem.setResults(results);
                    solvedProblemRepository.save(solvedProblem);
                }

                try {
                    resultsRepository.save(results);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
                }
                // result 만들어내기 03.11 (23:33) -> result 저장후, mistake solvedproblem 연결 완료 -> QA 부족 체크하기.
                CheckListResponse responses = new CheckListResponse(results.getResultId(), correctList, incorrectList);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", responses));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));


    }

    // 시험 연결된거 삭제하기. 
    public ResponseEntity<?> deleteTest(HttpServletRequest request, Long examId){
        Long userId = authService.getUserId(request);
        Optional<Exam> existingWordSets = examRepository.findByUser_UserIdAndExamId(userId, examId);
        if(existingWordSets.isPresent()){
            Exam tests = existingWordSets.get();
            DeleteExamResponse response = new DeleteExamResponse(examId, tests.getWordsets().getSetName(), Instant.now());
            try {
                examRepository.deleteById(examId);              
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(internalError, "서버 에러입니다."));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
    }


    public String getAIDescription(List<String> wordList, List<String> meaningList){
        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = new GeminiRequestDto();
        StringBuilder processedSentence = new StringBuilder();
        for(int i = 0;i<wordList.size();i++){
            int[] choices = {1, 2, 3, 4};
            int correctAnswer = choices[new Random().nextInt(choices.length)]; // 랜덤 선택
            processedSentence.append(wordList.get(i)).append("에 대한 의미를 묻는 객관식 문제를 만들어줘.")
            .append(wordList.get(i)).append("의 의미를 '").append(meaningList.get(i)).append("'로 하고, 이를 정답으로 설정해줘. ")
            .append("나머지 보기는 ").append(wordList.get(i)).append("와 관련 없는 단어의 뜻으로 만들어줘. ")
            .append("선택지의 품사는 '").append(meaningList.get(i)).append("'와 동일하게 맞춰줘. ")
            .append("정답 선택지의 ID 값은 ").append(correctAnswer).append("로 설정해줘. ");
        }
        processedSentence.append("JSON 형식으로 다음과 같이 만들어줘: ")
        .append("{\"question\":\"객관식 문제의 질문 내용\", ")
        .append("\"answer\":\"").append('1').append("\", ")
        .append("\"options\":[")
        .append("{\"id\":\"1\",\"text\":\"보기1\"}, ")
        .append("{\"id\":\"2\",\"text\":\"보기2\"}, ")
        .append("{\"id\":\"3\",\"text\":\"보기3\"}, ")
        .append("{\"id\":\"4\",\"text\":\"보기4\"}")
        .append("]}")
        .append("마지막에는 배열로 만들어줘.")
        .append("제공된 항목만 사용하고, 새로운 항목이나 문항을 임의로 만들지 마세요");
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

    private List<GenerateExamListResponse> parsingData(List<Word> words, Long number){
        Long problemNumber = number;             // 문제를 response로 낼 때는 몇번이지 알려줘야하기에.
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
        List<GenerateExamListResponse> responseDtos = new ArrayList<>();
        for(int i=0;i<objectArray.length() && problemNumber <= 20;i++){
            List<QuestionRequest> responses = new ArrayList<>();
            JSONArray newObject = objectArray.getJSONObject(i).getJSONArray("options"); // options에 나오는거까지 찍힘.
            String question = objectArray.getJSONObject(i).getString("question");
            String answer = objectArray.getJSONObject(i).getString("answer");
            System.out.println(answer);
            for(Object obj : newObject){
                JSONObject jsonObj = (JSONObject) obj;
                QuestionRequest response = new QuestionRequest(jsonObj.getString("id"), jsonObj.getString("text"));
                responses.add(response);
            }
            GenerateExamListResponse responseDto = new GenerateExamListResponse(problemNumber, responses, question, Integer.parseInt(answer));    
            problemNumber = problemNumber + 1;
            Collections.shuffle(responseDto.getMultipleChoice());     
            responseDtos.add(responseDto);   
        }
        return responseDtos;
    }

    public List<QuestionRequest> parseMultipleChoice(String multipleChoiceJson){
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


// 2025.4.26 야간 연등때 해야할 일
// 1. 테스트쪽 코드 10개단위로 수정하기. 
// 2. logic 한 번씩 보면서 불필요한 것 수정하기.