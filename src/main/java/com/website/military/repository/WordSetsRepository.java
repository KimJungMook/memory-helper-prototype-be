package com.website.military.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.WordSets;

public interface WordSetsRepository extends JpaRepository<WordSets, Long>{
        Optional<WordSets> findBysetName(String setName);
        List<WordSets> findByUser_UserId(Long userId);
}
