package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.WordSetMapping;

public interface WordSetsMappingRepository extends JpaRepository<WordSetMapping, Long>{
}
