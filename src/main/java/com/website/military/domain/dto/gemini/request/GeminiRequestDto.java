package com.website.military.domain.dto.gemini.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequestDto {

    private List<Content> contents;
    @Data
    public class Content {
        List<Part> parts;
        public Content(String text){
            parts = new ArrayList<>();
            Part part = new Part(text);
            parts.add(part);
        }
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public class Part {
            private String text;
        }   
    }
    
    public void createGeminiReqDto(String text){
        this.contents = new ArrayList<>();
        Content content = new Content(text);
        contents.add(content);
    }
}
