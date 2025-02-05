package com.website.military.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.website.military.domain.Entity.WordSetMapping;

public interface WordSetsMappingRepository extends JpaRepository<WordSetMapping, Long>{
    Optional<WordSetMapping> findByWord_WordIdAndWordsets_SetId(Long wordId, Long setId);
}
