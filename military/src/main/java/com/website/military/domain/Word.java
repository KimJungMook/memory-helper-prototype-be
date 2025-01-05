package com.website.military.domain;
import java.util.ArrayList;
import java.util.Date;

import jakarta.persistence.Entity;
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
    private int setId; // 단어셋 고유 ID 외래키
}
