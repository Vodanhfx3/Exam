package com.lms.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.exam.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}