package com.website.military.domain.Entity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.website.military.config.converter.StringListConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
    
    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> noun;

    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> verb;

    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> adjective;

    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> adverb;

    private Instant createAt;
    
    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 외래키

    @JsonManagedReference // 중복 순환 해결.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "word")
    private List<WordSetMapping> wordsetmapping;
    //   @JsonManagedReference // 중복 순환 해결.
    //   @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    //   private List<TestProblems> testproblems;
    //   @JsonManagedReference // 중복 순환 해결.
    //   @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    //   private List<Mistakes> mistakes;

    public Word(String word, List<String> noun, List<String> verb, List<String> adjective, List<String> adverb, User user){
        this.word = word;
        this.noun = (noun == null || noun.isEmpty() || (noun.size() == 1 && noun.get(0).trim().isEmpty())) ? new ArrayList<>() : noun;
        this.verb = (verb == null || verb.isEmpty() || (verb.size() == 1 && verb.get(0).trim().isEmpty())) ? new ArrayList<>() : verb;
        this.adjective = (adjective == null || adjective.isEmpty() || (adjective.size() == 1 && adjective.get(0).trim().isEmpty())) ? new ArrayList<>() : adjective;
        this.adverb = (adverb == null || adverb.isEmpty() || (adverb.size() == 1 && adverb.get(0).trim().isEmpty())) ? new ArrayList<>() : adverb;
        this.user = user;
        this.createAt = Instant.now();
    }
}
