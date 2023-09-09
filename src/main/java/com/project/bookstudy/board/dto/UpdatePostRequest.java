package com.project.bookstudy.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePostRequest {

    private Long postId;
    private String subject;
    private String contents;
    private Long categoryId;

    @Builder
    public UpdatePostRequest(String subject, String contents, Long categoryId) {
        this.subject = subject;
        this.contents = contents;
        this.categoryId = categoryId;
    }
}
