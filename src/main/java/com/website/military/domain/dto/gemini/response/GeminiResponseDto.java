package com.website.military.domain.dto.gemini.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDto {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
    }
    @Data
    public static class Content {
        private List<Parts> parts;
        private String role;
    }
    @Data
    public static class Parts{
        private String text;
    }
}
