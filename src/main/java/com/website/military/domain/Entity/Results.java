package com.website.military.domain.Entity;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Results {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", updatable = false)
    private Long resultId;
    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 외래키

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Tests tests; // 외래키

    private int score;
    private Instant submittedAt;

    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "results", cascade = CascadeType.PERSIST)
    private List<Mistakes> mistakes;

    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "results", cascade = CascadeType.PERSIST)
    private List<SolvedProblems> solvedProblems;

    public Results(User user, Tests tests, int score, List<Mistakes> mistakes, List<SolvedProblems> solvedProblems){
        this.user = user;
        this.tests = tests;
        this.score = score;
        this.submittedAt = Instant.now();
        this.mistakes = mistakes;
        this.solvedProblems = solvedProblems;
    }
}
