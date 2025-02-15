package com.website.military.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.website.military.domain.Entity.GptWordSetMapping;

public interface GptWordSetMappingRepository extends JpaRepository<GptWordSetMapping, Long>{
    Optional<GptWordSetMapping> findByGptword_GptWordIdAndWordsets_SetId(Long wordId, Long setId);
    @Query("SELECT gwsm FROM GptWordSetMapping gwsm JOIN FETCH gwsm.gptword WHERE gwsm.wordsets.setId = :setId") // 이런것도 알아두기.
    List<GptWordSetMapping> findAllByWordsets_SetId(@Param("setId") Long setId);
}
