package com.website.military.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



import com.website.military.domain.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
