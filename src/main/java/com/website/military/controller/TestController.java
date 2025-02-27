package com.website.military.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.service.TestService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/exam")
public class TestController {
    
    @Autowired
    private TestService testService;



    // POST 
    @PostMapping("/{setId}")
    public ResponseEntity<?> generateExamList(HttpServletRequest request, @PathVariable("setId") Long setId) {
        return testService.generateExamList(request, setId);
    }
}
