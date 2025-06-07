package com.website.military.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.website.military.domain.Entity.Exam;


public interface ExamRepository extends JpaRepository<Exam, Long>{
    Optional<Exam> findByUser_UserIdAndExamId(Long userId, Long examId);
    List<Exam> findByUser_UserId(Long userId);
}
