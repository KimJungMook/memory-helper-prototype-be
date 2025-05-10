package com.website.military.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Word;

public interface WordRepository extends JpaRepository<Word, Long>{
    Optional<Word> findByWord(String word);
    Optional<Word> findFirstByWordAndUser_UserIdOrderByCreateAtDesc(String word, Long userId);
}
