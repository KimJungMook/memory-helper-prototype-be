package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Tests;

public interface TestsRepository extends JpaRepository<Tests, Long>{
}
