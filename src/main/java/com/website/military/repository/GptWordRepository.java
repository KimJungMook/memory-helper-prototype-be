package com.website.military.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.GptWord;

public interface GptWordRepository extends JpaRepository<GptWord, Long>{
    Optional<GptWord> findByWord(String word);
}
