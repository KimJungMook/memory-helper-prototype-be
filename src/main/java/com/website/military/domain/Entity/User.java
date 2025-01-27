package com.website.military.domain.Entity;


import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long userId;
    private String username;
    private String email;
    private String password;
    private Date createdAt;
    private Date updatedAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<WordSets> wordsets;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Tests> tests;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Results> results;


    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
