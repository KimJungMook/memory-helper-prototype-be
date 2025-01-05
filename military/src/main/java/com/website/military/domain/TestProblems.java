package com.website.military.domain;
import java.util.ArrayList;

import jakarta.persistence.Entity;
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
    private int problemId;
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Tests tests; // 외래키
    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word words; // 외래키
    private String correctOption;
    private ArrayList<String> options;
}
