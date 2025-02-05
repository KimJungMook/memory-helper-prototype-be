package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.domain.dto.word.request.ExistWordDto;
import com.website.military.domain.dto.word.request.UpdateMeaningDto;
import com.website.military.domain.dto.word.response.AddWordToWordSetResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.service.WordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/word")
@Tag(name = "Word", description = "단어관련 API")
public class WordController {
    @Autowired
    private WordService wordService;

    // GET

    // POST
    @Operation(summary = "exist to word", description = "단어가 있는지 체크")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "EXIST",
            content = {@Content(schema = @Schema(implementation = ExistWordResponseDto.class))}),
        @ApiResponse(responseCode = "200", description = "해당하는 단어가 DB에 없습니다."),
        @ApiResponse(responseCode = "401", description = "토큰에 해당하는 사용자가 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/exists")
    public ResponseEntity<?> existWord(@RequestBody ExistWordDto dto, HttpServletRequest request) {
        return wordService.existWord(dto, request);
    }

    @PostMapping("/spelling-error")
    public ResponseEntity<?> correctSpelling(@RequestBody ExistWordDto dto ){
        return wordService.correctSpelling(dto.getWord());
    }

    @Operation(summary = "add Word to Wordsets", description = "단어를 단어세트에 넣기.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = AddWordToWordSetResponseDto.class))}),
        @ApiResponse(responseCode = "401", description = "단어셋을 만든 사람과 사용하는 사용자가 다릅니다."),
        @ApiResponse(responseCode = "401", description = "토큰에 해당하는 사용자가 없습니다."),
        @ApiResponse(responseCode = "400", description = "단어셋의 입력이 잘못되었습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/{setId}")
    public ResponseEntity<?> addWordToWordSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("setId") Long setId, 
    @RequestBody AddWordToWordSetDto dto, HttpServletRequest request) {
        return wordService.addWordToWordSet(setId, dto, request);
    }

    // PATCH(PUT)
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMeaning(
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id,
    @RequestBody UpdateMeaningDto dto,HttpServletRequest request){
        return wordService.updateMeaning(id, dto, request);
    }

    // DELETE
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteWord(
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id, HttpServletRequest request){
        return wordService.deleteWord(id, request);
    }

}
