package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
