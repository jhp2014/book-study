package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.Enrollment;

import java.util.List;

public interface CustomEnrollmentRepository {

    public List<Enrollment> findByStudyGroupIdWithPaymentWithMember(Long id);
}
