package com.project.bookstudy.board.dto.post;

import com.project.bookstudy.board.dto.file.CreateFileRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreatePostRequest {

    private String subject;
    private String contents;
    private Long categoryId;
    private Long memberId;
    private Long studyGroupId;

    private List<CreateFileRequest> files;
}
