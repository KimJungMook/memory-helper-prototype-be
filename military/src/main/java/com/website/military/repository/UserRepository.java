package com.website.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
