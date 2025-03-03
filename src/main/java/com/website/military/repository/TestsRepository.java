package com.website.military.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Tests;

public interface TestsRepository extends JpaRepository<Tests, Long>{
    Optional<Tests> findByUser_UserIdAndTestId(Long userId, Long testId);
}
