package com.website.military.domain;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tests {
    private int testId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 외래키
    @ManyToOne
    @JoinColumn(name = "set_id")
    private WordSets wordSets;  // 외래키
    private int testType;
    private Date createdAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tests")
    private List<TestProblems> testproblems;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tests")
    private List<Results> results;
}
