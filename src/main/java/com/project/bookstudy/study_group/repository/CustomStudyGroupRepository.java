package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomStudyGroupRepository {

    Optional<StudyGroup> findByIdWithLeader(Long id);
    Optional<StudyGroup> findByIdWithEnrollments(Long id);

    Optional<StudyGroup> findByIdWithEnrollmentWithAll(Long id);
    Page<StudyGroup> searchStudyGroupWithLeader(Pageable pageable, StudyGroupSearchCond cond);
}
