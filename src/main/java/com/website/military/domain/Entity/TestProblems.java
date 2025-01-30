package com.website.military.domain.Entity;
import java.util.ArrayList;

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
@Table(name = "testproblems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestProblems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id", updatable = false)
    private Long problemId;
    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Tests tests; // 외래키
    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word; // 외래키
    private String correctOption;
    private ArrayList<String> options;
}
