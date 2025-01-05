package com.website.military.domain;

import jakarta.persistence.Entity;
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
    private int mistakesId;
    @ManyToOne
    @JoinColumn(name = "result_id")
    private Results results; // 외래키
    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word words;  // 외래키
    private String userAnswer; 
}
