package com.website.military.config.util;

import java.util.ArrayList;
import java.util.List;

import com.website.military.domain.dto.response.WordClassResponse;

public class CommonUtils {
    public static List<WordClassResponse> meaningResponse(List<String> noun,List<String> verb,List<String> adjective,List<String> adverb) {
        List<WordClassResponse> responseList = new ArrayList<>();
        for (String word : noun) {
            if (word == null || word.isBlank()) continue;
            WordClassResponse response = new WordClassResponse();
            response.setType("noun");
            response.setValue(word);
            responseList.add(response);
        }
        for (String word : verb) {
            if (word == null || word.isBlank()) continue;
            WordClassResponse response = new WordClassResponse();
            response.setType("verb");
            response.setValue(word);
            responseList.add(response);
        }
        for (String word : adjective) {
            if (word == null || word.isBlank()) continue;
            WordClassResponse response = new WordClassResponse();
            response.setType("adjective");
            response.setValue(word);
            responseList.add(response);
        }
        for (String word : adverb) {
            if (word == null || word.isBlank()) continue;
            WordClassResponse response = new WordClassResponse();
            response.setType("adverb");
            response.setValue(word);
            responseList.add(response);
        }
        return responseList;
    }

}
