package com.website.military.domain.Entity;
import java.util.Date;
import java.util.List;

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
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Results {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", updatable = false)
    private Long resultId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 외래키
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Tests tests; // 외래키
    private int score;
    private Date submittedAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "results")
    private List<Mistakes> mistakes;
}
