package com.website.military.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Results;

public interface ResultsRepository extends JpaRepository<Results, Long>{
    Optional<Results> findByUser_UserIdAndResultId(Long userId, Long resultId);
    List<Results> findByUser_UserId(Long userId);
}
