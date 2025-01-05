package com.website.military.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mistakes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mistakes {
    private int mistakesId;
    private int resultId; // 외래키
    private int wordId;  // 외래키
    private String userAnswer; 
}
