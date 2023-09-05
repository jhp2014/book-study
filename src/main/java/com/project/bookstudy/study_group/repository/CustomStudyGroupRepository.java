package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStudyGroupRepository {

    StudyGroup findByIdWithLeader(Long id);
    Page<StudyGroup> searchStudyGroup(Pageable pageable);
}
