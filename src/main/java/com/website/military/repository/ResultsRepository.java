package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Results;

public interface ResultsRepository extends JpaRepository<Results, Long>{
}
