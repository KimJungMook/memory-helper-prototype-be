package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.word.request.ExistWordDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.service.WordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = ExistWordResponseDto.class),
                    examples = @ExampleObject(value = "{\n"
                    + "\"code\": \"OK\",\n"
                    + "\"data\": {\n"
                    + "  \"wordId\": 1,\n"
                    + "  \"word\": \"word\",\n"
                    + "  \"meaning\": [\n"
                    + "    {\"type\": \"noun\", \"value\": \"단어\"},\n"
                    + "  ],\n"
                    + "  \"gpt\": false\n"
                    + "}\n"
                    + "}")
                )}),
        @ApiResponse(responseCode = "201", description = "해당하는 단어가 DB에 없으므로, GPT에 해당하는 결과를 불러줌.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                    examples = @ExampleObject(value = "{\n"
                    + "\"code\": \"OK\",\n"
                    + "\"data\": {\n"
                    + "  \"meaning\": [\n"
                    + "    {\"type\": \"noun\", \"value\": \"단어\"},\n"
                    + "  ],\n"
                    + "  \"gpt\": true\n"
                    + "}\n"
                    + "}")
                )}
                ),
        @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/exists")
    public ResponseEntity<?> existWord(@Valid @RequestBody ExistWordDto dto, HttpServletRequest request) {
        return wordService.existWord(dto, request);
    }

    @Operation(summary = "스펠링 체크", description = "스펠링을 체크를 해주는 메서드, 정확도가 조금 낮음.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": \"word\""
                + "}")
            )}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/spelling-error")
    public ResponseEntity<?> correctSpelling(@RequestBody ExistWordDto dto ){
        return wordService.correctSpelling(dto.getWord());
    }

    // PATCH(PUT)
    // @Operation(summary = "단어 의미 변경", description = "단어의 의미를 바꾸고 싶을 때 사용하는 메서드")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "OK",
    //         content = {@Content(schema = @Schema(implementation = UpdateMeaningResponseDto.class),
    //             examples = @ExampleObject(value = "{\n"
    //             + "\"code\": \"OK\",\n"
    //             + "\"data\": {\n"
    //             + "  \"wordId\": 1,\n"
    //             + "  \"meaning\": [\n"
    //             + "    {\"type\": \"noun\", \"value\": \"단어\"},\n"
    //             + "  ],\n"
    //             + "  \"gpt\": false\n"
    //             + "}\n"
    //             + "}")
    //             )}),
    //     @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
    //             ))}),
    //     @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
    //             ))}),
    //     @ApiResponse(responseCode = "500", description = "서버 에러",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
    //             ))})
    // })
    // @PatchMapping("/{id}")
    // public ResponseEntity<?> updateMeaning(
    // @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    // @PathVariable("id") Long id,
    // @RequestBody UpdateMeaningDto dto,HttpServletRequest request){
    //     return wordService.updateMeaning(id, dto, request);
    // }
    
    // // DELETE
    // @Operation(summary = "단어 삭제", description = "단어를 DB에서 삭제하고 싶을 때 사용하는 메서드")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "DELETE",
    //         content = {@Content(schema = @Schema(implementation = DeleteWordResponseDto.class),
    //             examples = @ExampleObject(value = "{\n"
    //             + "\"code\": \"OK\",\n"
    //             + "\"data\": {\n"
    //             + "  \"wordId\": 1,\n"
    //             + "  \"word\": \"word\",\n"
    //             + "  \"meaning\": [\n"
    //             + "    {\"type\": \"noun\", \"value\": \"단어\"},\n"
    //             + "  ],\n"
    //             + "  \"gpt\": false\n"
    //             + "}\n"
    //             + "}")
    //             )}),
    //     @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
    //             ))}),
    //     @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
    //             ))}),
    //     @ApiResponse(responseCode = "500", description = "서버 에러",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
    //             ))})
    // })
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteWord(
    // @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    // @PathVariable("id") Long id, HttpServletRequest request){
    //     return wordService.deleteWord(id, request, false);
    // }

    // // DELETE
    // @Operation(summary = "Gpt단어 삭제", description = "Gpt단어를 DB에서 삭제하고 싶을 때 사용하는 메서드")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "DELETE",
    //         content = {@Content(schema = @Schema(implementation = DeleteWordResponseDto.class),
    //             examples = @ExampleObject(value = "{\n"
    //             + "\"code\": \"OK\",\n"
    //             + "\"data\": {\n"
    //             + "  \"wordId\": 1,\n"
    //             + "  \"word\": \"word\",\n"
    //             + "  \"meaning\": [\n"
    //             + "    {\"type\": \"noun\", \"value\": \"단어\"},\n"
    //             + "  ],\n"
    //             + "  \"gpt\": true\n"
    //             + "}\n"
    //             + "}")
    //             )}),
    //     @ApiResponse(responseCode = "400", description = "해당하는 단어가 없습니다.",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"BAD_REQUEST\": { \"message\": \"잘못된 요청입니다.\" } }"
    //             ))}),
    //     @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
    //             ))}),
    //     @ApiResponse(responseCode = "500", description = "서버 에러",
    //         content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
    //             examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
    //             ))})
    // })
    // @DeleteMapping("/gpt/{id}")
    // public ResponseEntity<?> deleteGptWord(
    // @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    // @PathVariable("id") Long id, HttpServletRequest request){
    //     return wordService.deleteWord(id, request, true);
    // }
}
