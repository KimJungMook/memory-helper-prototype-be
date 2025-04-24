package com.website.military.config.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.website.military.domain.dto.response.WordClassResponse;

public class CommonUtils {
    public static List<WordClassResponse> meaningResponse(List<String> noun,List<String> verb,List<String> adjective,List<String> adverb) {
        List<WordClassResponse> responseList = new ArrayList<>();
        for(int i = 0; i<noun.size(); i++){
            WordClassResponse response = new WordClassResponse();
            response.setType("noun");
            response.setValue(noun.get(i));
            responseList.add(response);
        }
        for(int i = 0; i<verb.size(); i++){
            WordClassResponse response = new WordClassResponse();
            response.setType("verb");
            response.setValue(verb.get(i));
            responseList.add(response);
        }
        for(int i = 0; i<adjective.size(); i++){
            WordClassResponse response = new WordClassResponse();
            response.setType("adjective");
            response.setValue(adjective.get(i));
            responseList.add(response);
        }
        for(int i = 0; i<adverb.size(); i++){
            WordClassResponse response = new WordClassResponse();
            response.setType("adverb");
            response.setValue(adverb.get(i));
            responseList.add(response);
        }
        return responseList;
    }

}
