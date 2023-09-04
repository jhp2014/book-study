package com.project.bookstudy.study_group.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StudyGroupParam {

    private String subject;
    private String contents;
    private String contentsDetail;
    private int maxSize;
    private Long price;
    private LocalDateTime studyStartAt;
    private LocalDateTime studyEndAt;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentEndAt;

    @Builder
    public StudyGroupParam(String subject, String contents, String contentsDetail, int maxSize, Long price, LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt) {
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
