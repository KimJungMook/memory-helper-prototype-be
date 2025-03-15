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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "testproblems")
@Data
@NoArgsConstructor
public class TestProblems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id", updatable = false)
    private Long problemId;

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Tests tests; // 외래키

    
    private String question;

    @Column(columnDefinition = "json")
    private String multipleChoice;


    private Long problemNumber;

    @Column(columnDefinition = "CHAR(1)")
    private char answer;
    
    @OneToOne(mappedBy = "testProblem") // 1대1 관계의 반대편
    private SolvedProblems solvedProblem;

    @OneToOne(mappedBy = "testProblem") // 1대1 관계의 반대편
    private Mistakes mistakes;

    public TestProblems(Tests tests, String multipleChoice, String question, Long problemNumber, char answer){
        this.tests = tests;
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.problemNumber = problemNumber;
        this.answer = answer;
    }
    
    public void setSolvedProblem(SolvedProblems solvedProblem) {
        this.solvedProblem = solvedProblem;
    }
    
    public void setSolvedProblem(Mistakes mistakes) {
        this.mistakes = mistakes;
    }
}
