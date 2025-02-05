package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.wordsets.request.ChangeSetNameDto;
import com.website.military.domain.dto.wordsets.request.WordSetsDto;
import com.website.military.domain.dto.wordsets.response.RegisterResponseDto;
import com.website.military.domain.dto.wordsets.response.WordSetsResponseDto;
import com.website.military.service.WordSetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/wordsets")
@Tag(name = "WordSet", description = "단어세트 관련 API")
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
                    schema = @Schema(implementation=WordSetsResponseDto.class)
                )
            ) ),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("")
    public ResponseEntity<?> getWordSets(HttpServletRequest request) {
        return wordSetService.getWordSets(request);
    }

    // POST
    @Operation(summary = "add to wordSets", description = "단어세트 만들기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = RegisterResponseDto.class))}),
        @ApiResponse(responseCode = "400", description = "이미 존재한 세트 이름"),
        @ApiResponse(responseCode = "400", description = "세트가 만들어지지 않았습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("")
    public ResponseEntity<?> registerWordSets(@RequestBody WordSetsDto dto, HttpServletRequest request) {
        return wordSetService.registerWordSets(dto, request);
    }
    
    // PUT(patch)
    @PatchMapping("/name/{id}")
    @Operation(summary = "단어셋 이름 변경", description = "ID에 해당하는 단어셋의 이름을 변경합니다.")
    public ResponseEntity<?> changeSetName(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id,
    @RequestBody ChangeSetNameDto dto,HttpServletRequest request){
        return wordSetService.changeSetName(id, dto.getSetName() ,request);
    }
    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "단어셋 삭제", description = "ID에 해당하는 단어셋을 삭제합니다.")
    public ResponseEntity<?> deleteWordSets(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id, 
    HttpServletRequest request){
        return wordSetService.deleteWordSets(id, request);
    }

    @DeleteMapping("/{setId}/words/{wordId}")
    public ResponseEntity<?> detachWordFromSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64"))     
    @PathVariable("setId") Long setId, 
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("wordId")Long wordId, HttpServletRequest request){
        return wordSetService.detachWordFromSet(setId, wordId, request);
    }

}
