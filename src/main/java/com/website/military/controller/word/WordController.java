package com.website.military.controller.word;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.Entity.Word;
import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.domain.dto.word.request.ExistWordDto;
import com.website.military.service.WordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/word")
public class WordController {
    @Autowired
    private WordService wordService;

    // GET
    @GetMapping("/exists")
    public ResponseEntity<?> existWord(@RequestParam ExistWordDto dto, HttpServletRequest request) {
        ResponseEntity<?> entity = wordService.existWord(dto, request);
        return entity;
    }
    

    // POST
    @Operation(summary = "add Word to Wordsets", description = "단어를 단어세트에 넣기.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = Word.class))}),
        @ApiResponse(responseCode = "400", description = "단어셋을 만든 사람과 사용하는 사용자가 다릅니다."),
        @ApiResponse(responseCode = "400", description = "토큰에 해당하는 사용자가 없습니다."),
        @ApiResponse(responseCode = "400", description = "단어셋의 입력이 잘못되었습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/{setId}")
    public ResponseEntity<?> addWordToWordSet(@PathVariable Long setId, @RequestBody AddWordToWordSetDto dto, HttpServletRequest request) {
        //TODO: process POST request
        ResponseEntity<?> entity = wordService.addWordToWordSet(setId, dto, request);
        return entity;
    }

}
