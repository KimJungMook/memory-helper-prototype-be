package com.website.military.domain.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mistakes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mistakes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mistake_id", updatable = false)
    private Long mistakesId;

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "result_id")
    private Results results; // 외래키
    
    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;  // 외래키

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "gpt_word_id")
    private GptWord gptword;  // 외래키

    private String userAnswer; 
}
