package com.project.bookstudy.board.dto.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ToString
public class CreateCategoryRequest {

    private Long parentCategoryId;
    private Long studyGroupId;
    private String subject;

    @Builder
    private CreateCategoryRequest(Long parentCategoryId, Long studyGroupId, String subject) {
        this.parentCategoryId = parentCategoryId;
        this.studyGroupId = studyGroupId;
        this.subject = subject;
    }
}
