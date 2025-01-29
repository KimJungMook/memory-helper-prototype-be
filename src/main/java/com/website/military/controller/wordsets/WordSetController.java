package com.website.military.controller.wordsets;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.dto.wordsets.request.WordSetsDto;
import com.website.military.service.wordsets.WordSetService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/wordsets")
public class WordSetController {
    @Autowired
    private WordSetService wordSetService;

    // GET
    @GetMapping("")
    public ResponseEntity<?> getWordSets(HttpServletRequest request) {
        ResponseEntity<?> entity = wordSetService.getWordSets(request);
        return entity;
    }

    // POST

    @PostMapping("/register")
    public ResponseEntity<?> RegisterWordSets(@RequestBody WordSetsDto dto, HttpServletRequest request) {
        //TODO: process POST request
        ResponseEntity<?> entity = wordSetService.RegisterWordSets(dto, request);
        return entity;
    }
    
    

}
