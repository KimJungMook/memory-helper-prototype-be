package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Text;

public interface TextRepository extends JpaRepository<Text, Long>{
}