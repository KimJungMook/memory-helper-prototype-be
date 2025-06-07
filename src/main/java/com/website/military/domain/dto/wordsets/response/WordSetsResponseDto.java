package com.website.military.domain.dto.wordsets.response;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.website.military.domain.Entity.Exam;

import lombok.Data;

@Data
public class WordSetsResponseDto {
    private Long setId;
    private String setName;
    private int wordCount;
    private int testSetsCount;
    private Instant createdAt;
    private List<Long> testIds;

    public WordSetsResponseDto(Long setId, String setName, Instant createdAt, int wordCount, List<Exam> tests){
        this.setId = setId;
        this.setName = setName;
        this.createdAt = createdAt;
        this.wordCount = wordCount;
        this.testSetsCount = tests.size();
        List<Long> testIdList = new ArrayList<>();
        for(Exam test : tests){
            Long id = test.getExamId();
            testIdList.add(id);
        }
        testIds = testIdList;
    }
}
