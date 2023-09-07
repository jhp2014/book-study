package com.project.bookstudy.study_group.domain.param;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateStudyGroupParam {

    private Long id;
    private String subject;
    private Long price;
    private String contents;
    private String contentsDetail;
    private int maxSize;
    private LocalDateTime studyStartAt;
    private LocalDateTime studyEndAt;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentEndAt;

    @Builder
    private UpdateStudyGroupParam(Long id, String subject, String contents, String contentsDetail, int maxSize, Long price, LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt) {
        this.id = id;
        this.subject = subject;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.maxSize = maxSize;
        this.price = price;
        this.studyStartAt = studyStartAt;
        this.studyEndAt = studyEndAt;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
    }
}
