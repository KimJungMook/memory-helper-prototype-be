package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.TestProblems;

public interface TestProblemsRepository extends JpaRepository<TestProblems, Long>{
}
