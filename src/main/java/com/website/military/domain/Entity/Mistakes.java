package com.website.military.domain.Entity;

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
    @ManyToOne
    @JoinColumn(name = "result_id")
    private Results results; // 외래키
    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;  // 외래키
    private String userAnswer; 
}
