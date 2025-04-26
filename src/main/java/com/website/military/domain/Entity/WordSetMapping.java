package com.website.military.domain.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wordsetmapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordSetMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable=false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // 중복 순환 해결.
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // 중복 순환 해결.
    @JoinColumn(name = "set_id", nullable = false)
    private WordSets wordsets;

    public WordSetMapping(Word word, WordSets wordsets){
        this.word = word;
        this.wordsets = wordsets;
    }
}
