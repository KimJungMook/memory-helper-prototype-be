package com.website.military.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.exam.request.ChangeExamName;
import com.website.military.domain.dto.exam.request.CheckRequest;
import com.website.military.domain.dto.exam.response.DeleteExamResponse;
import com.website.military.domain.dto.exam.response.GenerateProblemResponse;
import com.website.military.domain.dto.exam.response.GetAllExamListResponse;
import com.website.military.domain.dto.exam.response.GetProblemsResponse;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.wordsets.request.ChangeSetNameDto;
import com.website.military.service.ExamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class ExamController {
    
    @Autowired
    private ExamService testService;

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

    @GetMapping("/{page}/{pageSize}")
    public ResponseEntity<?> getAllExamList(HttpServletRequest request, @PathVariable("page") Long page, @PathVariable("pageSize") Long pageSize) {
        return testService.getAllExamListPage(request, page, pageSize);
    }

    @Operation(summary = "시험 하나를 불러오는 API", description = "생성된 시험 하나를 불러오는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=GetProblemsResponse.class)
                ),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": ["
                + "  {"
                + "    \"problemId\": 331,"
                + "    \"problemNumber\": 1,"
                + "    \"question\": \"banana의 의미는 무엇입니까?\","
                + "    \"multipleChoice\": ["
                + "      {\"id\": \"1\", \"meaning\": \"바나나\"},"
                + "      {\"id\": \"2\", \"meaning\": \"포도\"},"
                + "      {\"id\": \"3\", \"meaning\": \"사과\"},"
                + "      {\"id\": \"4\", \"meaning\": \"오렌지\"}"
                + "    ],"
                + "    \"answer\": \"1\""
                + "  },"
                + "  {"
                + "    \"problemId\": 332,"
                + "    \"problemNumber\": 2,"
                + "    \"question\": \"word의 의미는 무엇입니까?\","
                + "    \"multipleChoice\": ["
                + "      {\"id\": \"1\", \"meaning\": \"그림을 그리다\"},"
                + "      {\"id\": \"2\", \"meaning\": \"노래를 부르다\"},"
                + "      {\"id\": \"4\", \"meaning\": \"책을 읽다\"},"
                + "      {\"id\": \"3\", \"meaning\": \"단어를 입력하다\"}"
                + "    ],"
                + "    \"answer\": \"2\""
                + "  },"
                + "  {"
                + "    \"problemId\": 333,"
                + "    \"problemNumber\": 3,"
                + "    \"question\": \"rabbit의 의미는 무엇입니까?\","
                + "    \"multipleChoice\": ["
                + "      {\"id\": \"2\", \"meaning\": \"새\"},"
                + "      {\"id\": \"1\", \"meaning\": \"개\"},"
                + "      {\"id\": \"4\", \"meaning\": \"토끼\"},"
                + "      {\"id\": \"3\", \"meaning\": \"고양이\"}"
                + "    ],"
                + "    \"answer\": \"1\""
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
    @GetMapping("/{examId}")
    public ResponseEntity<?> getTestProblems(HttpServletRequest request, @PathVariable("examId") Long examId){
        return testService.getTestProblems(request, examId);
    }

    // POST
    @Operation(summary = "시험을 생성하는 API", description = "AI 이용해서 시험 생성하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=GenerateProblemResponse.class)
                ),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": {"
                + "    \"testId\": 1,"
                + "    \"examList\": ["
                + "      {"
                + "        \"problemNumber\": 1,"
                + "        \"multipleChoice\": ["
                + "          {\"id\": \"1\", \"meaning\": \"편지\"},"
                + "          {\"id\": \"2\", \"meaning\": \"글자\"},"
                + "          {\"id\": \"3\", \"meaning\": \"서한\"},"
                + "          {\"id\": \"4\", \"meaning\": \"문자\"}"
                + "        ],"
                + "        \"question\": \"다음 중 'letter'의 의미로 가장 적절한 것은 무엇일까요?\"," 
                + "        \"answer\": \"1\""
                + "      },"
                + "      {"
                + "        \"problemNumber\": 2,"
                + "        \"multipleChoice\": ["
                + "          {\"id\": \"2\", \"meaning\": \"결코\"},"
                + "          {\"id\": \"1\", \"meaning\": \"항상\"},"
                + "          {\"id\": \"4\", \"meaning\": \"대개\"},"
                + "          {\"id\": \"3\", \"meaning\": \"때때로\"}"
                + "        ],"
                + "        \"question\": \"다음 중 'usually'의 의미와 가장 가까운 것은 무엇입니까?\"," 
                + "        \"answer\": \"3\""
                + "      },"
                + "      {"
                + "        \"problemNumber\": 3,"
                + "        \"multipleChoice\": ["
                + "          {\"id\": \"1\", \"meaning\": \"매우\"},"
                + "          {\"id\": \"3\", \"meaning\": \"조금\"},"
                + "          {\"id\": \"4\", \"meaning\": \"약간\"},"
                + "          {\"id\": \"2\", \"meaning\": \"거의\"}"
                + "        ],"
                + "        \"question\": \"다음 중 'very'의 뜻과 가장 가까운 것은 무엇입니까?\"," 
                + "        \"answer\": \"3\""
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

    @PostMapping("/check/{examId}") // -> 채점을 할 때 result는 생성됌.
    public ResponseEntity<?> checkAnswers(HttpServletRequest request, @PathVariable("examId")Long examId, @Valid @RequestBody CheckRequest dto) {
        return testService.checkAnswers(request, examId, dto.getCheckedAnswers());
    }

    // PATCH
      @PatchMapping("/name/{testId}")
    public ResponseEntity<?> patchTestName(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("testId") Long testId,
    @RequestBody ChangeExamName dto,HttpServletRequest request){
        return testService.patchTestName(request, testId, dto.getTestName());
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
    @DeleteMapping("/{examId}")
    public ResponseEntity<?> deleteExam(HttpServletRequest request, @PathVariable("examId")Long examId){
        return testService.deleteTest(request, examId);
    }
}


/*
 * {
  "code": "OK",
  "data": {
    "resultId": 1,
    "correctList": [
      {
        "problemId": 8,
        "problemNumber": 8,
        "multipleChoice": [
          {
            "id": "3",
            "meaning": "과정"
          },
          {
            "id": "4",
            "meaning": "방법"
          },
          {
            "id": "2",
            "meaning": "결과"
          },
          {
            "id": "1",
            "meaning": "요소"
          }
        ],
        "userAnswer": 1,
        "correctAnswer": 1
      },
      {
        "problemId": 9,
        "problemNumber": 9,
        "multipleChoice": [
          {
            "id": "2",
            "meaning": "단어"
          },
          {
            "id": "3",
            "meaning": "구절"
          },
          {
            "id": "4",
            "meaning": "글자"
          },
          {
            "id": "1",
            "meaning": "문장"
          }
        ],
        "userAnswer": 2,
        "correctAnswer": 2
      }
    ],
    "incorrectList": [
      {
        "problemId": 5,
        "problemNumber": 5,
        "multipleChoice": [
          {
            "id": "2",
            "meaning": "운 나쁘게도"
          },
          {
            "id": "3",
            "meaning": "슬프게도"
          },
          {
            "id": "4",
            "meaning": "운 좋게도"
          },
          {
            "id": "1",
            "meaning": "불운하게도"
          }
        ],
        "userAnswer": 1,
        "correctAnswer": 4
      },
      {
        "problemId": 6,
        "problemNumber": 6,
        "multipleChoice": [
          {
            "id": "1",
            "meaning": "읽다"
          },
          {
            "id": "3",
            "meaning": "듣다"
          },
          {
            "id": "4",
            "meaning": "말하다"
          },
          {
            "id": "2",
            "meaning": "쓰다"
          }
        ],
        "userAnswer": 2,
        "correctAnswer": 1
      },
      {
        "problemId": 10,
        "problemNumber": 10,
        "multipleChoice": [
          {
            "id": "4",
            "meaning": "취소하다"
          },
          {
            "id": "3",
            "meaning": "중지하다"
          },
          {
            "id": "2",
            "meaning": "실행하다"
          },
          {
            "id": "1",
            "meaning": "생성하다"
          }
        ],
        "userAnswer": 1,
        "correctAnswer": 2
      }
    ]
  }
}
 */