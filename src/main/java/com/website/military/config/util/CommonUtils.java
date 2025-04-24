package com.website.military.config.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.website.military.domain.dto.response.WordClassResponse;

public class CommonUtils {
    public static List<WordClassResponse> meaningResponse(List<String> noun,List<String> verb,List<String> adjective,List<String> adverb) {
        List<WordClassResponse> responseList = new ArrayList<>(Arrays.asList(
            new WordClassResponse(), new WordClassResponse(), new WordClassResponse(), new WordClassResponse()
        ));
        responseList.get(0).setType("noun");
        responseList.get(0).setValue(noun);
        responseList.get(1).setType("verb");
        responseList.get(1).setValue(verb);
        responseList.get(2).setType("adjective");
        responseList.get(2).setValue(adjective);
        responseList.get(3).setType("adverb");
        responseList.get(3).setValue(adverb);
        return responseList;
    }

}
