package com.website.military.domain.dto.word.request;

import java.util.List;

import com.website.military.domain.dto.response.WordClassResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMeaningDto {
    @Schema(
        description = "뜻 입력",
        example = "[\n" +
                  "{\n " +
                  "  \"type\": \"noun\",\n" +
                  "  \"value\": \n" + 
                  "    \"단어\"\n" +
                  "\n" +
                  "}," +
                  "{\n " +
                  "  \"type\": \"verb\",\n" +
                  "  \"value\": \n" +
                  "    \"단어를 입력하다\"\n" +
                  "\n" +
                  "}" +
                  "]"
    )
    private List<WordClassResponse> meaning;
}
