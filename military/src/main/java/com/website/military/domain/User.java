package com.website.military.domain;


import java.util.Date;

import jakarta.persistence.Entity;
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
    
}
