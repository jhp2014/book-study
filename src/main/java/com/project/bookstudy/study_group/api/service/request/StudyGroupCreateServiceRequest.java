package com.project.bookstudy.study_group.api.service.request;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class StudyGroupCreateServiceRequest {

    private String subject;
    private String contents;
    private String contentsDetail;
    private int maxSize;
    private Long price;
    private LocalDateTime studyStartAt;
    private LocalDateTime studyEndAt;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentEndAt;
    private Long memberId;

    @Builder
    private StudyGroupCreateServiceRequest(String subject, String contents, String contentsDetail, int maxSize, Long price, LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt, Long memberId) {
        this.subject = subject;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.maxSize = maxSize;
        this.price = price;
        this.studyStartAt = studyStartAt;
        this.studyEndAt = studyEndAt;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
        this.memberId = memberId;
    }

    public StudyGroup toEntityWithLeader(Member leader) {
        return StudyGroup.builder()
                .leader(leader)
                .studyStartAt(this.studyStartAt)
                .studyEndAt(this.studyEndAt)
                .recruitmentStartAt(this.recruitmentStartAt)
                .recruitmentEndAt(this.recruitmentEndAt)
                .subject(this.subject)
                .contents(this.contents)
                .contentsDetail(this.contentsDetail)
                .price(this.price)
                .maxSize(this.maxSize)
                .build();

    }

    public void validateDateOrder(LocalDateTime now) {
        if (this.getStudyStartAt().isAfter(this.getStudyEndAt())
                || this.getRecruitmentStartAt().isAfter(this.getRecruitmentEndAt())
                || this.getRecruitmentStartAt().isAfter(this.getStudyStartAt())
                || now.isAfter(this.getRecruitmentStartAt())) {
            throw new IllegalStateException(ErrorMessage.INVALID_DATE_RANGE_ERROR.getMessage());
        }
    }
}
