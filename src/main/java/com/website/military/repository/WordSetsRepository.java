package com.website.military.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.website.military.domain.Entity.WordSets;



public interface WordSetsRepository extends JpaRepository<WordSets, Long>{
        Optional<WordSets> findBysetName(String setName);
        List<WordSets> findByUser_UserId(Long userId);
        Optional<WordSets> findByUser_UserIdAndSetId(Long userId, Long setId);

        @Transactional
        @Modifying
        @Query("UPDATE WordSets s SET s.wordCount = s.wordCount + 1 WHERE s.setId = :setId")
        void incrementWordCount(@Param("setId") Long setId);
}
