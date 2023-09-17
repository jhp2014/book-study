package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomStudyGroupRepository {

    Optional<StudyGroup> findByIdWithEnrollmentWithAll(Long id);
    Page<StudyGroup> searchStudyGroupFetchLeader(Pageable pageable, StudyGroupSearchCond cond);
}
