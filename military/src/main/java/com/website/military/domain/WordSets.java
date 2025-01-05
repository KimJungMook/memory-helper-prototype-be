package com.website.military.domain;

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
@Table(name = "wordsets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordSets {
    private int setId;
    private String setName;
    private Date createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 외래키
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wordsets")
    private List<Word> words;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wordsets")
    private List<Tests> tests;
}
