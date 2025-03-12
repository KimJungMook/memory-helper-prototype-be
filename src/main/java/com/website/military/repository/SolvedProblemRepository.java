package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.SolvedProblems;

public interface SolvedProblemRepository extends JpaRepository<SolvedProblems, Long>{
    
}
