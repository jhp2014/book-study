package com.project.bookstudy.comment.dto;

import com.project.bookstudy.comment.domain.param.CreateCommentParam;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCommentRequest {

    private Long postId;
    private Long memberId;
    private Long parentId;
    private String content;


    @Builder
    private CreateCommentRequest(Long postId, Long memberId, Long parentId, String content) {
        this.postId = postId;
        this.memberId = memberId;
        this.parentId = parentId;
        this.content = content;
    }

    public CreateCommentParam toCommentParam() {
        return CreateCommentParam.builder()
                .content(this.content)
                .build();
    }
}
