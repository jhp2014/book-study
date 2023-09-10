package com.project.bookstudy.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CreatePostRequest {

    private String subject;
    private String contents;

    private Long categoryId;
    private Long memberId;
    private Long studyGroupId;
    private List<CreateAndUpdateFileRequest> files;

    @Builder
    public CreatePostRequest(String subject, String contents, List<CreateAndUpdateFileRequest> files, Long categoryId, Long memberId, Long studyGroupId) {
        this.subject = subject;
        this.contents = contents;
        this.categoryId = categoryId;
        this.memberId = memberId;
        this.studyGroupId = studyGroupId;
        this.files = files;
    }
}
