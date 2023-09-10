package com.project.bookstudy.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdatePostRequest {

    private Long postId;
    private String subject;
    private String contents;
    private Long categoryId;

    private List<CreateAndUpdateFileRequest> requests;

    @Builder
    public UpdatePostRequest(String subject, String contents, Long categoryId, List<CreateAndUpdateFileRequest> requests) {
        this.subject = subject;
        this.contents = contents;
        this.categoryId = categoryId;
        this.requests = requests;
    }
}
