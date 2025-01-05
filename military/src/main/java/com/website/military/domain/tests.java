package com.website.military.domain;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class tests {
    private int testId;
    private int userId; // 외래키
    private int setId;  // 외래키
    private int testType;
    private Date createdAt;
    
}
