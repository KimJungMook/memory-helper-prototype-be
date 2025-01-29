package com.website.military.domain.Entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

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

    @JsonBackReference // 중복 순환 해결.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 외래키
    @OneToMany(mappedBy="wordsets", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WordSetMapping> wordsetmapping;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wordsets")
    private List<Tests> tests;

    public WordSets(String setName){
        this.setName = setName;
        this.createdAt = new Date();
    }
}
