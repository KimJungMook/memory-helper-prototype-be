package com.website.military.service;


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

import com.website.military.domain.Entity.GptWord;
import com.website.military.domain.Entity.GptWordSetMapping;
import com.website.military.domain.Entity.Word;
import com.website.military.domain.Entity.WordSetMapping;
import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.gemini.request.GeminiRequestDto;
import com.website.military.domain.dto.gemini.response.GeminiResponseDto;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.test.response.GenerateExamListResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponseDto;
import com.website.military.repository.WordSetsRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TestService {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private WordSetsRepository wordSetsRepository;

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
            List<WordSetMapping> mapping = wordSets.getWordsetmapping();
            List<GptWordSetMapping> gptmapping = wordSets.getGptwordsetMappings();
            int length = mapping.size() + gptmapping.size();
            if(length < 20){
                Collections.shuffle(mapping);
                Collections.shuffle(gptmapping);
                List<GenerateExamListResponseDto> responseDtos = new ArrayList<>();
                // 여기서부터 다시 로직을 짜서 이어가야할듯. 로직은 WordSetMapping에 있는 단어를 골라서 단어와 뜻을 가져오고, 이를 gpt에 돌려서 다시 세팅한 후 리스트로 만들어서 넣기.
                // 로직은 어느정도 완성이 됐다. 하지만, 아직 동사만 적용하는 한계가 존재함.(02.27)
                // 순서도 어느정도 셔플을 해서 보내는게 좋을 것 같음.(02. 27)
                // testproblem에 넣어야하는데, 아직 넣는 코드가 존재하지 않음.
                for(WordSetMapping tmp : mapping){
                    Word tmpWord = tmp.getWord();
                    GenerateExamListResponseDto responseDto = parsingData(tmpWord);
                    responseDtos.add(responseDto);
                }
                for(GptWordSetMapping tmp : gptmapping){
                    GptWord tmpWord = tmp.getGptword();
                    GenerateExamListResponseDto responseDto = parsingData(tmpWord);
                    responseDtos.add(responseDto);
                }
                return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK", responseDtos));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 접근입니다."));
    }

    public String getAIDescription(String word, String meaning){
        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = new GeminiRequestDto();
        String processedSentence = word + "에 대한 의미를 묻는 객관식 문제를 만들어줘." + word + "의 의미를 " + meaning + "를 정답으로 해서 만들어줘. " +
        "다른 보기는 " + word + "와는 전혀 관련이 없는 단어의 뜻으로 만들어줘. " +
        "선택지의 품사는 '" + meaning + "'와 똑같이 맞춰줘. " +
        "JSON 형식으로 다음과 같이 만들어줘. 객관식의 보기는 id와 text를 가지게 해주고, 정답은 id로 통일해줘.";
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

    public GenerateExamListResponseDto parsingData(GptWord word){
        List<GenerateExamListResponse> responses = new ArrayList<>();
        String s = getAIDescription(word.getWord(), word.getVerb().get(0));
        String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
        JSONObject object = new JSONObject(cleanJson);
        JSONArray newObject = object.getJSONArray("options"); // options에 나오는거까지 찍힘.
        String question = object.getString("question");
        String answer = object.getString("answer");
        for(Object obj : newObject){
            JSONObject jsonObj = (JSONObject) obj;
            System.out.println(jsonObj.toString());
            GenerateExamListResponse response = new GenerateExamListResponse(jsonObj.getString("id"), jsonObj.getString("text"));
            responses.add(response);
        }
        GenerateExamListResponseDto responseDto = new GenerateExamListResponseDto(responses, question, answer);
        return responseDto;
    }

    public GenerateExamListResponseDto parsingData(Word word){
        List<GenerateExamListResponse> responses = new ArrayList<>();
        String s = getAIDescription(word.getWord(), word.getVerb().get(0));
        String cleanJson = s.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", ""); // 불필요한 json이런게 들어감.
        JSONObject object = new JSONObject(cleanJson);
        JSONArray newObject = object.getJSONArray("options"); // options에 나오는거까지 찍힘.
        String question = object.getString("question");
        String answer = object.getString("answer");
        for(Object obj : newObject){
            JSONObject jsonObj = (JSONObject) obj;
            System.out.println(jsonObj.toString());
            GenerateExamListResponse response = new GenerateExamListResponse(jsonObj.getString("id"), jsonObj.getString("text"));
            responses.add(response);
        }
        GenerateExamListResponseDto responseDto = new GenerateExamListResponseDto(responses, question, answer);
        return responseDto;
    }
}




// 질문 예시

// book에 대한 의미를 물어보는 객관식 문제를 만들거야. 
// book의 의미를 예약하다를 정답으로 해서 만들어주고, 다른 보기는 book와는 전혀 관련이 없는 단어의 뜻으로 만들어주고, 선택지의 품사는 예약하다와 똑같이 맞춰줘. 
// json 형태로 만들어줘


// [{"id":"A","text":"묶다"},{"id":"B","text":"뛰어넘다"},{"id":"C","text":"밝히다"},{"id":"D","text":"흔들다"}]
// {"question":"다음 중 'band'의 핵심 의미와 가장 가까운 것은 무엇입니까?","answer":"A","options":[{"id":"A","text":"묶다"},{"id":"B","text":"뛰어넘다"},{"id":"C","text":"밝히다"},{"id":"D","text":"흔들다"}]}