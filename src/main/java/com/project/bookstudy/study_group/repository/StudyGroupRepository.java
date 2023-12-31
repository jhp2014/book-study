package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, CustomStudyGroupRepository {

}
