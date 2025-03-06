package com.website.military.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.test.response.DeleteExamResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponseDto;
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

@RestController
@RequestMapping("/api/exam")
@Tag(name = "EXAM", description = "시험 관련 API")
public class TestController {
    
    @Autowired
    private TestService testService;



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
                + "\"data\": ["
                + "    {"
                + "      \"problemNumber\": 1,"
                + "      \"list\": ["
                + "        {\"id\": \"A\", \"meaning\": \"생각\"},"
                + "        {\"id\": \"B\", \"meaning\": \"생수\"},"
                + "        {\"id\": \"C\", \"meaning\": \"우유\"},"
                + "        {\"id\": \"D\", \"meaning\": \"마음\"}"
                + "      ],"
                + "      \"question\": \"다음 중 'milk'의 의미와 가장 가까운 것은 무엇입니까?\"," 
                + "      \"answer\": \"C\""
                + "    }"
                + "  ]"
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


// {
//     "code": "OK",
//     "data": {
//       "testId": 6,
//       "setName": "베델기우스",
//       "deletedAt": "2025-03-05T14:18:10.164746581Z"
//     }
//   }