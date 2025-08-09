package com.website.military.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.website.military.domain.Entity.Exam;


public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam>{
    Optional<Exam> findByUser_UserIdAndExamId(Long userId, Long examId);
    List<Exam> findByUser_UserId(Long userId);
    List<Exam> findByUser_UserIdAndWordsets_SetId(Long userId, Long setId);
    Page<Exam> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pagable);
}
