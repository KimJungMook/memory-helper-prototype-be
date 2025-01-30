package com.website.military.domain.Entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "word")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id", updatable = false)
    private Long wordId;
    private String word;
    private String meaning;
    private ArrayList<String> synonyms;
    private ArrayList<String> antonyms;
    private Date createAt;
    private Date updatedAt;
    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "word")
    private List<WordSetMapping> wordsetmapping;
    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    private List<TestProblems> testproblems;
    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    private List<Mistakes> mistakes;
}
