package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.WordSets;

public interface WordSetsRepository extends JpaRepository<WordSets, Long>{
}
