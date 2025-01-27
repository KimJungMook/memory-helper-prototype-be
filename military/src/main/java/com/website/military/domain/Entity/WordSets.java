package com.website.military.domain.Entity;

import java.util.Date;
import java.util.List;

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
@Table(name = "wordsets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordSets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id", updatable = false)
    private Long setId;
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
