package com.project.bookstudy.board.dto.post;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class PostSearchCond {

    @NotNull
    private Long studyGroupId;
    private Long categoryId;
    private String subject;
    private String contents;

    @Builder
    private PostSearchCond(Long studyGroupId, Long categoryId, String subject, String contents) {
        this.studyGroupId = studyGroupId;
        this.categoryId = categoryId;
        this.subject = subject;
        this.contents = contents;
    }
}
