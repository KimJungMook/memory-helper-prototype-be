package com.website.military.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.test.response.DeleteExamResponse;
import com.website.military.domain.dto.test.response.GenerateExamListResponseDto;
import com.website.military.service.TestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=GenerateExamListResponseDto.class)
                )
            ) ),
        @ApiResponse(responseCode = "401", description = "잘못된 접근입니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/{setId}")
    public ResponseEntity<?> generateExamList(HttpServletRequest request,@PathVariable("setId") Long setId) {
        return testService.generateExamList(request, setId);
    }

    // DELETE
    @Operation(summary = "시험 삭제하는 API", description = "시험 ID를 이용해 삭제하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = DeleteExamResponse.class))}),
        @ApiResponse(responseCode = "400", description = "잘못된 접근입니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{testId}")
    public ResponseEntity<?> deleteExam(HttpServletRequest request, @PathVariable("testId")Long testId){
        return testService.deleteTest(request, testId);
    }
}
