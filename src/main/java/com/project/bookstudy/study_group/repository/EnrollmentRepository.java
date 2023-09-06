package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, CustomEnrollmentRepository {
}
