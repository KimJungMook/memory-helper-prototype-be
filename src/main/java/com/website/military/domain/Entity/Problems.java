package com.website.military.domain.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
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
@Table(name = "problems")
@Data
@NoArgsConstructor
public class Problems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id", updatable = false)
    private Long problemId;

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "examId")
    private Exam exam; // 외래키

    
    private String question;

    @Column(columnDefinition = "json")
    private String multipleChoice;


    private Long problemNumber;

    private int userAnswer;

    private int answer;

    
    @OneToOne(mappedBy = "problems", cascade = CascadeType.ALL, orphanRemoval = true) // 1대1 관계의 반대편
    private SolvedProblems solvedProblem;

    @OneToOne(mappedBy = "problems", cascade = CascadeType.ALL, orphanRemoval = true) // 1대1 관계의 반대편
    private Mistakes mistakes;

    public Problems(Exam exam, String multipleChoice, String question, Long problemNumber, int answer){
        this.exam = exam;
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.problemNumber = problemNumber;
        this.answer = answer;
    }

    public Problems(Problems problems, int userAnswer){
        this.problemId = problems.getProblemId();
        this.exam = problems.getExam();
        this.question = problems.getQuestion();
        this.multipleChoice = problems.getMultipleChoice();
        this.problemNumber = problems.getProblemNumber();
        this.userAnswer = userAnswer;
        this.answer = problems.getAnswer();
        this.solvedProblem = problems.getSolvedProblem();
        this.mistakes = problems.getMistakes();
    }
    
    public void setSolvedProblem(SolvedProblems solvedProblem) {
        this.solvedProblem = solvedProblem;
    }
    
    public void setSolvedProblem(Mistakes mistakes) {
        this.mistakes = mistakes;
    }
}
