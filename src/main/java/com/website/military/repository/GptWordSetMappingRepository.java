package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.GptWordSetMapping;

public interface GptWordSetMappingRepository extends JpaRepository<GptWordSetMapping, Long>{
    
}
