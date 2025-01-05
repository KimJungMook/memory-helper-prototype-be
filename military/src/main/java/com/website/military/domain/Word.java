package com.website.military.domain;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private int wordId;
    private String word;
    private String meaning;
    private ArrayList<String> synonyms;
    private ArrayList<String> antonyms;
    private Date createAt;
    private Date updatedAt;
    @ManyToOne
    @JoinColumn(name = "set_id")
    private WordSets wordSets; // 단어셋 고유 ID 외래키
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    private List<TestProblems> testproblems;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    private List<Mistakes> mistakes;
}
