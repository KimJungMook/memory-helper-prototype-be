package com.website.military.controller.wordsets;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.Entity.WordSets;
import com.website.military.domain.dto.wordsets.request.WordSetsDto;
import com.website.military.service.WordSetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/wordsets")
public class WordSetController {
    @Autowired
    private WordSetService wordSetService;

    // GET
    @Operation(summary = "find to wordSets", description = "내가 만든 단어세트 찾기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=WordSets.class)
                )
            ) ),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("")
    public ResponseEntity<?> getWordSets(HttpServletRequest request) {
        ResponseEntity<?> entity = wordSetService.getWordSets(request);
        return entity;
    }

    // POST
    @Operation(summary = "add to wordSets", description = "단어세트 만들기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = WordSets.class))}),
        @ApiResponse(responseCode = "400", description = "이미 존재한 세트 이름"),
        @ApiResponse(responseCode = "400", description = "세트가 만들어지지 않았습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/register")
    public ResponseEntity<?> RegisterWordSets(@RequestBody WordSetsDto dto, HttpServletRequest request) {
        //TODO: process POST request
        ResponseEntity<?> entity = wordSetService.RegisterWordSets(dto, request);
        return entity;
    }
    
    

}
