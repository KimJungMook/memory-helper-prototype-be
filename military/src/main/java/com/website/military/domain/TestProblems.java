package com.website.military.domain;
import java.util.ArrayList;

import jakarta.persistence.Entity;
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
    private int testId; // 외래키
    private int wordId; // 외래키
    private String correctOption;
    private ArrayList<String> options;
}
