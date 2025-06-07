package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Problems;

public interface ProblemsRepository extends JpaRepository<Problems, Long>{
}
