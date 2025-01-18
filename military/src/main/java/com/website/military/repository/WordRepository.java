package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.website.military.domain.Word;

public interface WordRepository extends JpaRepository<Word, Long>{
    
}
