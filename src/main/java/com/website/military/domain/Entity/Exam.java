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
@Table(name = "exam")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id", updatable = false)
    private Long examId;

    private String examName;

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 외래키

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "set_id")
    private WordSets wordsets;  // 외래키

    private int testType; // 0번 객관식, 1번 주관식, 2번 빈칸뚫기 
    private Instant createdAt;
    private Instant submittedAt;
    private int problemCount;

    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Problems> problems;

    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exam",  cascade = CascadeType.ALL)
    private List<Results> results;
    
    public Exam(String setName, User user, WordSets wordsets,int testType, int testCount){
        this.examName = setName + "의 시험" + testCount;
        this.user = user;
        this.wordsets = wordsets;
        this.testType = testType;
        this.createdAt = Instant.now();
    }
}
