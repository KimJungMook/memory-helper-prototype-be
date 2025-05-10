package com.website.military.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.service.ResultsService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/result")
@Tag(name = "RESULT", description = "시험결과 관련 API")
public class ResultsController {
 
    @Autowired
    private ResultsService resultsService;

    @GetMapping("/{id}") 
    public ResponseEntity<?> getGradingResult(HttpServletRequest request, @PathVariable("id") Long id){
        return resultsService.getGradingResult(request, id);
    }

    @GetMapping("/all") 
    public ResponseEntity<?> getAllGradingResult(HttpServletRequest request){
        return resultsService.getAllGradingResult(request);
    }
}
