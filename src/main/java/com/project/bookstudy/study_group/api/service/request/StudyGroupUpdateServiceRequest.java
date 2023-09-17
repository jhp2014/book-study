package com.project.bookstudy.study_group.api.service.request;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StudyGroupUpdateServiceRequest {
    private Long studyGroupId;
    private String subject;
    private String contents;
    private String contentsDetail;
    private Integer maxSize;
    private Long price;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentEndAt;
    private LocalDateTime studyStartAt;
    private LocalDateTime studyEndAt;

    @Builder
    public StudyGroupUpdateServiceRequest(Long studyGroupId, String subject, String contents, String contentsDetail, Integer maxSize, Long price, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt, LocalDateTime studyStartAt, LocalDateTime studyEndAt) {
        this.studyGroupId = studyGroupId;
        this.subject = subject;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.maxSize = maxSize;
        this.price = price;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
        this.studyStartAt = studyStartAt;
        this.studyEndAt = studyEndAt;
    }

    public StudyGroup toEntity() {
        return StudyGroup.builder()
                .subject(this.subject)
                .contents(this.contents)
                .contentsDetail(this.contentsDetail)
                .maxSize(this.maxSize)
                .price(this.price)
                .studyStartAt(this.studyStartAt)
                .studyEndAt(this.studyEndAt)
                .recruitmentStartAt(this.recruitmentStartAt)
                .recruitmentEndAt(this.recruitmentEndAt)
                .build();

    }

    public void validateDateOrder() {
        if (this.getStudyStartAt().isAfter(this.getStudyEndAt())
                || this.getRecruitmentStartAt().isAfter(this.getRecruitmentEndAt())
                || this.getRecruitmentStartAt().isAfter(this.getStudyStartAt())) {
            throw new IllegalStateException(ErrorMessage.INVALID_DATE_RANGE_ERROR.getMessage());
        }
    }

}
