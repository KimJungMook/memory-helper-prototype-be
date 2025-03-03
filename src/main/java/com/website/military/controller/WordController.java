package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.word.request.ExistWordDto;
import com.website.military.domain.dto.word.request.UpdateMeaningDto;
import com.website.military.domain.dto.word.response.DeleteWordResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.domain.dto.word.response.UpdateMeaningResponseDto;
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
    @Operation(summary = "단어 존재 체크", description = "단어가 있는지 체크를 해주는 메서드")
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

    @Operation(summary = "스펠링 체크", description = "스펠링을 체크를 해주는 메서드, 정확도가 조금 낮음.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = String.class))}),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/spelling-error")
    public ResponseEntity<?> correctSpelling(@RequestBody ExistWordDto dto ){
        return wordService.correctSpelling(dto.getWord());
    }

    // PATCH(PUT)
    @Operation(summary = "단어 의미 변경", description = "단어의 의미를 바꾸고 싶을 때 사용하는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = UpdateMeaningResponseDto.class))}),
        @ApiResponse(responseCode = "200", description = "해당하는 단어가 없습니다."),
        @ApiResponse(responseCode = "401", description = "토큰에 해당하는 사용자가 없습니다."),
        @ApiResponse(responseCode = "401", description = "단어를 만든 사람과 사용하는 사용자가 다릅니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMeaning(
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id,
    @RequestBody UpdateMeaningDto dto,HttpServletRequest request){
        return wordService.updateMeaning(id, dto, request);
    }
    
    // @Operation(summary = "Gpt단어 의미 변경", description = "Gpt단어의 의미를 바꾸고 싶을 때 사용하는 메서드")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "OK",
    //         content = {@Content(schema = @Schema(implementation = UpdateMeaningResponseDto.class))}),
    //     @ApiResponse(responseCode = "200", description = "해당하는 단어가 없습니다."),
    //     @ApiResponse(responseCode = "401", description = "토큰에 해당하는 사용자가 없습니다."),
    //     @ApiResponse(responseCode = "401", description = "단어를 만든 사람과 사용하는 사용자가 다릅니다."),
    //     @ApiResponse(responseCode = "500", description = "서버 에러")
    // })
    // @PatchMapping("/gpt/{id}")
    // public ResponseEntity<?> updateGptMeaning(
    // @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    // @PathVariable("id") Long id,
    // @RequestBody UpdateMeaningDto dto,HttpServletRequest request){
    //     return wordService.updateMeaning(id, dto, request, true);
    // }

    // DELETE
    @Operation(summary = "단어 삭제", description = "단어를 DB에서 삭제하고 싶을 때 사용하는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "DELETE",
            content = {@Content(schema = @Schema(implementation = DeleteWordResponseDto.class))}),
        @ApiResponse(responseCode = "200", description = "해당하는 단어가 없습니다."),
        @ApiResponse(responseCode = "401", description = "토큰에 해당하는 사용자가 없습니다."),
        @ApiResponse(responseCode = "401", description = "단어를 만든 사람과 삭제하는 사용자가 다릅니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWord(
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id, HttpServletRequest request){
        return wordService.deleteWord(id, request, false);
    }

    // DELETE
    @Operation(summary = "Gpt단어 삭제", description = "Gpt단어를 DB에서 삭제하고 싶을 때 사용하는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "DELETE",
            content = {@Content(schema = @Schema(implementation = DeleteWordResponseDto.class))}),
        @ApiResponse(responseCode = "200", description = "해당하는 단어가 없습니다."),
        @ApiResponse(responseCode = "401", description = "토큰에 해당하는 사용자가 없습니다."),
        @ApiResponse(responseCode = "401", description = "단어를 만든 사람과 삭제하는 사용자가 다릅니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/gpt/{id}")
    public ResponseEntity<?> deleteGptWord(
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id, HttpServletRequest request){
        return wordService.deleteWord(id, request, true);
    }
}
