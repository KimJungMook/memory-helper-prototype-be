package com.website.military.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.military.domain.Entity.Text;
import com.website.military.domain.dto.PostDto;
import com.website.military.repository.TextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/")
class TestController {
    @Autowired
    private TextRepository textRepository;
    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        List<Text> lists = textRepository.findAll();

        return ResponseEntity.ok().body(lists);
    }

    @PostMapping("/addition")
    public ResponseEntity<?> postData(@RequestBody PostDto dto) {
        //TODO: process POST request
        Text textEntity = null;
        textEntity = new Text(dto.getContent());
        textRepository.save(textEntity);
        return ResponseEntity.ok().body(textEntity);
    }
    

}