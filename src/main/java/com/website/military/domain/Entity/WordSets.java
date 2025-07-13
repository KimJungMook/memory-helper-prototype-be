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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wordsets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordSets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id", updatable = false)
    private Long setId;

    private String setName;

    private Instant createdAt;
    private Instant updatedAt;
    private int wordCount;
    @Column(columnDefinition = "INT DEFAULT 0")
    private int testCount;
    
    @JsonBackReference // 중복 순환 해결.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 외래키
    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy="wordsets", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WordSetMapping> wordsetmapping;

    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wordsets")
    private List<Exam> exams;

    
    public WordSets(String setName){
        this.setName = setName;
        this.createdAt = Instant.now();
    }
}
