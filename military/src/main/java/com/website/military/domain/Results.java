package com.website.military.domain;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Results {
    private int resultId;
    private int userId; // 외래키
    private int testId; // 외래키
    private int score;
    private Date submittedAt;
}
