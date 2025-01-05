package com.website.military.domain;

import java.util.Date;

import jakarta.persistence.Entity;
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
    private int userId; // 외래키
}
