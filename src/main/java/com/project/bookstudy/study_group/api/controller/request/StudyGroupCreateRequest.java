package com.project.bookstudy.study_group.api.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bookstudy.study_group.api.service.request.StudyGroupCreateServiceRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroupCreateRequest {

    @NotNull(message = "회원 정보는 필수 입니다.")
    private Long memberId;

    @NotBlank(message = "스터디 주제는 필수 입니다.")
    private String subject;

    @NotBlank(message = "스터디 내용은 필수 입니다.")
    private String contents;

    @NotBlank(message = "스터디 상세 정보는 필수 입니다.")
    private String contentsDetail;

    @Min(value = 1, message = "스터디 인원은 최소 한명 입니다.")
    private int maxSize;

    @PositiveOrZero(message = "스터디 가격은 0 이상 입니다.")
    private Long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime studyStartAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime studyEndAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentEndAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentStartAt;

    @Builder
    private StudyGroupCreateRequest(Long memberId, String subject, String contents, String contentsDetail, int maxSize, Long price, LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt) {
        this.memberId = memberId;
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

    public StudyGroupCreateServiceRequest toCreateServiceParam() {
        return StudyGroupCreateServiceRequest.builder()
                .subject(this.subject)
                .contents(this.contents)
                .contentsDetail(this.contentsDetail)
                .maxSize(this.maxSize)
                .price(this.price)
                .studyStartAt(this.studyStartAt)
                .studyEndAt(this.studyEndAt)
                .recruitmentStartAt(this.recruitmentStartAt)
                .recruitmentEndAt(this.recruitmentEndAt)
                .memberId(this.memberId)
                .build();
    }
}
