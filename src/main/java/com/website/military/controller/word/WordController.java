package com.website.military.controller.word;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.word.request.AddWordToWordSetDto;
import com.website.military.service.word.WordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/word")
public class WordController {
    @Autowired
    private WordService wordService;

    // GET


    // POST
    @PostMapping("")
    public ResponseEntity<?> postMethodName(@RequestBody AddWordToWordSetDto dto) {
        //TODO: process POST request
        ResponseEntity<?> entity = wordService.addWordToWordSet(dto);
        return entity;
    }
    
}
