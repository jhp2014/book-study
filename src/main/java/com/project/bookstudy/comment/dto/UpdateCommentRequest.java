package com.project.bookstudy.comment.dto;

import com.project.bookstudy.comment.domain.param.UpdateCommentParam;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateCommentRequest {

    private Long id;
    private String content;


    @Builder
    private UpdateCommentRequest(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public UpdateCommentParam getUpdateCommentParam() {
        return UpdateCommentParam.builder()
                .id(id)
                .content(content)
                .build();
    }

}
