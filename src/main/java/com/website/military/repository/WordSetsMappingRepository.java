package com.website.military.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.website.military.domain.Entity.WordSetMapping;

public interface WordSetsMappingRepository extends JpaRepository<WordSetMapping, Long>{
    Optional<WordSetMapping> findByWord_WordIdAndWordsets_SetId(Long wordId, Long setId);
    @Query("SELECT wsm FROM WordSetMapping wsm JOIN FETCH wsm.word WHERE wsm.wordsets.setId = :setId") // 이런것도 알아두기.
    List<WordSetMapping> findAllByWordsets_SetId(@Param("setId") Long setId);
}
