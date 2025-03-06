package com.website.military.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.domain.dto.word.response.AddWordToWordSetResponseDto;
import com.website.military.domain.dto.word.response.ExistWordResponseDto;
import com.website.military.domain.dto.wordsets.request.ChangeSetNameDto;
import com.website.military.domain.dto.wordsets.request.WordSetsDto;
import com.website.military.domain.dto.wordsets.response.DeleteResponseDto;
import com.website.military.domain.dto.wordsets.response.DetachResponse;
import com.website.military.domain.dto.wordsets.response.GetWordsBySetIdResponse;
import com.website.military.domain.dto.wordsets.response.RegisterResponseDto;
import com.website.military.domain.dto.wordsets.response.WordSetsResponseDto;
import com.website.military.service.WordSetService;

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
    @Operation(summary = "단어세트 찾기", description = "내가 만든 단어세트 찾아주는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=WordSetsResponseDto.class)
                ),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": ["
                + "  {"
                + "    \"setId\": 1,"
                + "    \"setName\": \"세트1\","
                + "    \"createdAt\": \"2025-03-05T14:43:12.031Z\""
                + "  },"
                + "  {"
                + "    \"setId\": 2,"
                + "    \"setName\": \"세트2\","
                + "    \"createdAt\": \"2025-03-05T14:44:12.031Z\""
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
    @GetMapping("")
    public ResponseEntity<?> getWordSets(HttpServletRequest request) {
        return wordSetService.getWordSets(request);
    }

    @Operation(summary = "단어세트안에있는 단어 리스트", description = "단어 세트안에 있는 단어 리스트를 불러오는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation=GetWordsBySetIdResponse.class)
                ),
                examples = @ExampleObject(value = "{"
                + "\"code\": \"OK\","
                + "\"data\": ["
                + "  {"
                + "    \"id\": 1,"
                + "    \"word\": \"book\","
                + "    \"noun\": [\"책\"],"
                + "    \"verb\": [\"예약하다\"],"
                + "    \"adjective\": [],"
                + "    \"adverb\": [],"
                + "    \"createdAt\": \"2025-02-12T14:45:52.175424Z\","
                + "    \"gpt\": true"
                + "  },"
                + "  {"
                + "    \"id\": 4,"
                + "    \"word\": \"letter\","
                + "    \"noun\": [\"문자\"],"
                + "    \"verb\": [\"편지를 쓰다\"],"
                + "    \"adjective\": [],"
                + "    \"adverb\": [],"
                + "    \"createdAt\": \"2025-02-22T05:32:31.575933Z\","
                + "    \"gpt\": false"
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getWordsBySetId(    
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id, HttpServletRequest request) {
        return wordSetService.getWordsBySetId(id, request);
    }
    

    // POST
    @Operation(summary = "단어 세트 생성", description = "단어세트 만들어주는 메서드")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = RegisterResponseDto.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"setId\": 1,\n"
                + "  \"setName\": \"세트이름1\"\n"
                + "}\n"
                + "}")
                )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
            ))})
    })
    @PostMapping("")
    public ResponseEntity<?> registerWordSets(@RequestBody WordSetsDto dto, HttpServletRequest request) {
        return wordSetService.registerWordSets(dto, request);
    }
    
    @Operation(summary = "이미 존재한 단어 단어세트에 넣기", description = "이미 존재한 단어 단어장에 넣어주는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = ExistWordResponseDto.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"id\": 1,\n"
                + "  \"word\": \"word\",\n"
                + "  \"noun\": \"[\"단어\"]\",\n"
                + "  \"verb\": \"[]\",\n"
                + "  \"adjective\": \"[]\",\n"
                + "  \"adverb\": \"[\"\"]\"\n"
                + "  \"isGpt\": false\n"
                + "}\n"
                + "}")
            )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/{setId}/word/{wordId}")
    public ResponseEntity<?> assignWordToSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("setId") Long setId, 
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("wordId") Long wordId, HttpServletRequest request ){
        return wordSetService.assignWordToSet(setId, wordId, request, false);
    }

    @Operation(summary = "이미 존재한 Gpt단어 단어세트에 넣기", description = "이미 존재한 Gpt단어 단어장에 넣어주는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = ExistWordResponseDto.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"wordId\": 1,\n"
                + "  \"word\": \"word\",\n"
                + "  \"noun\": \"[\"단어\"]\",\n"
                + "  \"verb\": \"[]\",\n"
                + "  \"adjective\": \"[]\",\n"
                + "  \"adverb\": \"[\"\"]\"\n"
                + "  \"isGpt\": true\n"
                + "}\n"
                + "}")
            )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
                ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
            content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
                examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
                ))})
    })
    @PostMapping("/{setId}/word/gpt/{wordId}")
    public ResponseEntity<?> assignGptWordToSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("setId") Long setId, 
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("wordId") Long wordId, HttpServletRequest request ){
        return wordSetService.assignWordToSet(setId, wordId, request, true);
    }

    @Operation(summary = "유저가 만든 단어 단어세트에 넣기", description = "존재하지 않는 단어를 단어장에 넣는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = AddWordToWordSetResponseDto.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"wordId\": 1,\n"
                + "  \"word\": \"word\",\n"
                + "  \"noun\": \"[\"단어\"]\",\n"
                + "  \"verb\": \"[]\",\n"
                + "  \"adjective\": \"[]\",\n"
                + "  \"adverb\": \"[\"\"]\"\n"
                + "}\n"
                + "}")
                )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
            ))})
    })
    @PostMapping("/{setId}")
    public ResponseEntity<?> addWordToWordSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("setId") Long setId, 
    @RequestBody AddWordToWordSetDto dto, HttpServletRequest request) {
        return wordSetService.addWordToWordSet(setId, dto, request, false);
    }


    @Operation(summary = "GPT 단어 단어세트에 넣기", description = "존재하지 않는 단어를 단어장에 넣는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = AddWordToWordSetResponseDto.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"wordId\": 1,\n"
                + "  \"word\": \"word\",\n"
                + "  \"noun\": \"[\"단어\"]\",\n"
                + "  \"verb\": \"[]\",\n"
                + "  \"adjective\": \"[]\",\n"
                + "  \"adverb\": \"[\"\"]\"\n"
                + "}\n"
                + "}")
                )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "401", description = "잘못된 접근입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"UNAUTHORIZE\", \"data\": { \"message\": \"잘못된 접근입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
            ))})
    })
    @PostMapping("/gpt/{setId}")
    public ResponseEntity<?> addGptWordToWordSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("setId") Long setId, 
    @RequestBody AddWordToWordSetDto dto, HttpServletRequest request) {
        return wordSetService.addWordToWordSet(setId, dto, request, true);
    }


    // PUT(patch)
    @Operation(summary = "세트 이름 변경", description = "세트 이름을 바꿔주는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = RegisterResponseDto.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"setId\": 1,\n"
                + "  \"setName\": \"세트이름1\"\n"
                + "}\n"
                + "}")
            )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
            ))})
    })
    @PatchMapping("/name/{id}")
    public ResponseEntity<?> changeSetName(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id,
    @RequestBody ChangeSetNameDto dto,HttpServletRequest request){
        return wordSetService.changeSetName(id, dto.getSetName() ,request);
    }
    
    // DELETE
    @Operation(summary = "단어세트 삭제", description = "ID에 해당하는 단어셋을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(implementation = DeleteResponseDto.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"setId\": 1,\n"
                + "  \"setName\": \"세트이름1\"\n"
                + "}\n"
                + "}")
            )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
            ))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWordSets(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("id") Long id, 
    HttpServletRequest request){
        return wordSetService.deleteWordSets(id, request);
    }

    @Operation(summary = "단어 단어장에서 삭제", description = "단어를 단어장에서 없애주는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "DELETE",
            content = {@Content(schema = @Schema(implementation = DetachResponse.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"wordId\": 1,\n"
                + "  \"word\": \"word\",\n"
                + "  \"noun\": \"[\"단어\"]\",\n"
                + "  \"verb\": \"[]\",\n"
                + "  \"adjective\": \"[]\",\n"
                + "  \"adverb\": \"[\"\"]\"\n"
                + "}\n"
                + "}")
            )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
            ))})
    })
    @DeleteMapping("/{setId}/word/{wordId}")
    public ResponseEntity<?> detachWordFromSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64"))     
    @PathVariable("setId") Long setId, 
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("wordId")Long wordId, HttpServletRequest request){
        return wordSetService.detachWordFromSet(setId, wordId, request, false);
    }

    @Operation(summary = "Gpt단어 단어장에서 삭제", description = "Gpt단어를 단어장에서 없애주는 api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "DELETE",
            content = {@Content(schema = @Schema(implementation = DetachResponse.class),
                examples = @ExampleObject(value = "{\n"
                + "\"code\": \"OK\",\n"
                + "\"data\": {\n"
                + "  \"wordId\": 1,\n"
                + "  \"word\": \"word\",\n"
                + "  \"noun\": \"[\"단어\"]\",\n"
                + "  \"verb\": \"[]\",\n"
                + "  \"adjective\": \"[]\",\n"
                + "  \"adverb\": \"[\"\"]\"\n"
                + "}\n"
                + "}")
            )}),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"data\": { \"message\": \"잘못된 요청입니다.\" } }"
            ))}),
        @ApiResponse(responseCode = "500", description = "서버 에러",
        content = {@Content(schema = @Schema(implementation = ResponseMessageDto.class),
            examples = @ExampleObject(value = "{\"code\": \"INTERNAL_SERVER\", \"data\": { \"message\": \"서버 에러\" } }"
            ))})
    })
    @DeleteMapping("/{setId}/gpt/word/{wordId}")
    public ResponseEntity<?> detachGptWordFromSet(
    @Parameter(description = "단어셋의 id", schema = @Schema(type = "integer", format = "int64"))     
    @PathVariable("setId") Long setId, 
    @Parameter(description = "단어의 id", schema = @Schema(type = "integer", format = "int64")) 
    @PathVariable("wordId")Long wordId, HttpServletRequest request){
        return wordSetService.detachWordFromSet(setId, wordId, request, true);
    }
    

}
