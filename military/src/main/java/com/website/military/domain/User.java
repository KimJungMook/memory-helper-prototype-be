package com.website.military.domain;


import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private int userId;
    private String username;
    private String email;
    private String password;
    private Date createdAt;
    private Date updatedAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<WordSets> wordSets;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Tests> tests;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Results> results;
}
