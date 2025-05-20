package com.website.military.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.test.request.CheckRequest;
import com.website.military.domain.dto.test.response.DeleteExamResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponseDto;
import com.website.military.domain.dto.test.response.GetAllExamListResponse;
import com.website.military.domain.dto.test.response.GetTestProblemsResponse;
import com.website.military.service.TestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/exam")
@Tag(name = "EXAM", description = "시험 관련 API")
public class TestController {
    
    @Autowired
    private TestService testService;

    // GET
    @Operation(summary = "시험세트 찾기", description = "내가 만든 시험세트 찾아주는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=GetAllExamListResponse.class)
                ),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": ["
                + "  {"
                + "    \"testId\": 1,"
                + "    \"createdAt\": \"2025-03-05T14:43:12.031Z\","
                + "    \"testType\": 0"
                + "  },"
                + "  {"
                + "    \"testId\": 2,"
                + "    \"createdAt\": \"2025-03-05T14:43:12.031Z\","
                + "    \"testType\": 0"
                + "  }"
                + "]"
                + "}")            
            )}),
       @ApiResponse(responseCode = "400", description = "잘못된 접근입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllExamList(HttpServletRequest request) {
        return testService.getAllExamList(request);
    }

    @Operation(summary = "시험 하나를 불러오는 API", description = "생성된 시험 하나를 불러오는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=GetTestProblemsResponse.class)
                ),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": ["
                + "  {"
                + "    \"problemId\": 331,"
                + "    \"problemNumber\": 1,"
                + "    \"question\": \"banana의 의미는 무엇입니까?\","
                + "    \"multipleChoice\": ["
                + "      {\"id\": \"D\", \"meaning\": \"바나나\"},"
                + "      {\"id\": \"C\", \"meaning\": \"포도\"},"
                + "      {\"id\": \"A\", \"meaning\": \"사과\"},"
                + "      {\"id\": \"B\", \"meaning\": \"오렌지\"}"
                + "    ],"
                + "    \"answer\": \"D\""
                + "  },"
                + "  {"
                + "    \"problemId\": 332,"
                + "    \"problemNumber\": 2,"
                + "    \"question\": \"word의 의미는 무엇입니까?\","
                + "    \"multipleChoice\": ["
                + "      {\"id\": \"C\", \"meaning\": \"그림을 그리다\"},"
                + "      {\"id\": \"D\", \"meaning\": \"노래를 부르다\"},"
                + "      {\"id\": \"A\", \"meaning\": \"책을 읽다\"},"
                + "      {\"id\": \"B\", \"meaning\": \"단어를 입력하다\"}"
                + "    ],"
                + "    \"answer\": \"B\""
                + "  },"
                + "  {"
                + "    \"problemId\": 333,"
                + "    \"problemNumber\": 3,"
                + "    \"question\": \"rabbit의 의미는 무엇입니까?\","
                + "    \"multipleChoice\": ["
                + "      {\"id\": \"D\", \"meaning\": \"새\"},"
                + "      {\"id\": \"C\", \"meaning\": \"개\"},"
                + "      {\"id\": \"A\", \"meaning\": \"토끼\"},"
                + "      {\"id\": \"B\", \"meaning\": \"고양이\"}"
                + "    ],"
                + "    \"answer\": \"A\""
                + "  }"
                + "]"
                + "}")
            )}),
        @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                    examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
                    ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getTestProblems(HttpServletRequest request, @PathVariable("id") Long id){
        return testService.getTestProblems(request, id);
    }

    // POST
    @Operation(summary = "시험을 생성하는 API", description = "AI 이용해서 시험 생성하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=GenerateExamListResponseDto.class)
                ),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": {"
                + "    \"testId\": 1,"
                + "    \"examList\": ["
                + "      {"
                + "        \"problemNumber\": 1,"
                + "        \"multipleChoice\": ["
                + "          {\"id\": \"A\", \"meaning\": \"편지\"},"
                + "          {\"id\": \"C\", \"meaning\": \"글자\"},"
                + "          {\"id\": \"D\", \"meaning\": \"서한\"},"
                + "          {\"id\": \"B\", \"meaning\": \"문자\"}"
                + "        ],"
                + "        \"question\": \"다음 중 'letter'의 의미로 가장 적절한 것은 무엇일까요?\"," 
                + "        \"answer\": \"B\""
                + "      },"
                + "      {"
                + "        \"problemNumber\": 2,"
                + "        \"multipleChoice\": ["
                + "          {\"id\": \"C\", \"meaning\": \"결코\"},"
                + "          {\"id\": \"A\", \"meaning\": \"항상\"},"
                + "          {\"id\": \"B\", \"meaning\": \"대개\"},"
                + "          {\"id\": \"D\", \"meaning\": \"때때로\"}"
                + "        ],"
                + "        \"question\": \"다음 중 'usually'의 의미와 가장 가까운 것은 무엇입니까?\"," 
                + "        \"answer\": \"B\""
                + "      },"
                + "      {"
                + "        \"problemNumber\": 3,"
                + "        \"multipleChoice\": ["
                + "          {\"id\": \"A\", \"meaning\": \"매우\"},"
                + "          {\"id\": \"B\", \"meaning\": \"조금\"},"
                + "          {\"id\": \"D\", \"meaning\": \"약간\"},"
                + "          {\"id\": \"C\", \"meaning\": \"거의\"}"
                + "        ],"
                + "        \"question\": \"다음 중 'very'의 뜻과 가장 가까운 것은 무엇입니까?\"," 
                + "        \"answer\": \"A\""
                + "      }"
                + "    ]"
                + "  }"
                + "}")
            )}),
        @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                    examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
                    ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/{setId}")
    public ResponseEntity<?> generateExamList(HttpServletRequest request,@PathVariable("setId") Long setId) {
        return testService.generateExamList(request, setId);
    }

    @PostMapping("/check/{testId}") // -> 채점을 할 때 result는 생성됌.
    public ResponseEntity<?> checkAnswers(HttpServletRequest request, @PathVariable("testId")Long testId, @Valid @RequestBody CheckRequest dto) {
        return testService.checkAnswers(request, testId, dto.getCheckedAnswers());
    }
    
    // DELETE
    @Operation(summary = "시험 삭제하는 API", description = "시험 ID를 이용해 삭제하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = DeleteExamResponse.class),
                examples = @ExampleObject(value = "{\"code\": \"OK\", \"data\": { \"testId\": \"1\", \"setName\": \"세트이름\", \"deletedAt\": \"2025-03-05T14:18:10.164746581Z\" } }"
                    ))}),
        @ApiResponse(responseCode = "400", description = "잘못된 접근입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @DeleteMapping("/{testId}")
    public ResponseEntity<?> deleteExam(HttpServletRequest request, @PathVariable("testId")Long testId){
        return testService.deleteTest(request, testId);
    }
}
