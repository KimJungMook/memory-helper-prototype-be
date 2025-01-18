package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.website.military.domain.Mistakes;

public interface MistakesRepository extends JpaRepository<Mistakes, Long>{
    
}
