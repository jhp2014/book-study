package com.project.bookstudy.board.dto;

import com.project.bookstudy.board.dmain.Category;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
public class CreatePostRequest {

    private String subject;
    private String contents;

    private Long categoryId;
    private Long memberId;
    private Long studyGroupId;

    @Builder
    public CreatePostRequest(String subject, String contents, Long categoryId, Long memberId, Long studyGroupId) {
        this.subject = subject;
        this.contents = contents;
        this.categoryId = categoryId;
        this.memberId = memberId;
        this.studyGroupId = studyGroupId;
    }
}
