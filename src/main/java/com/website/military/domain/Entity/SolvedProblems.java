package com.website.military.domain.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solvedproblems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolvedProblems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solved_problem_id", updatable = false)
    private Long solvedProblemId;

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "result_id")
    private Results results; // 외래키
    
    @OneToOne
    @JoinColumn(name = "problem_id", unique = true) // 1대1 관계
    private TestProblems testProblem;


    public SolvedProblems(TestProblems testProblem) {
        this.testProblem = testProblem;
        testProblem.setSolvedProblem(this); // 양방향 자동 설정
    }
}
