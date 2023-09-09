package com.project.bookstudy.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateCategoryRequest {

    private Long categoryId;
    private Long parentCategoryId;
    private String subject;

    @Builder
    private UpdateCategoryRequest(Long parentCategoryId, String subject) {
        this.parentCategoryId = parentCategoryId;
        this.subject = subject;
    }
}
