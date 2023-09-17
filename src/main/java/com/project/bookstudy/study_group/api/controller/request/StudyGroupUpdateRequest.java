package com.project.bookstudy.study_group.api.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bookstudy.study_group.api.service.request.StudyGroupUpdateServiceRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroupUpdateRequest {

    @NotNull(message = "수정할 스터디 그룹의 정보는 필수 입니다.")
    private Long studyGroupId;

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
    private LocalDateTime recruitmentStartAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentEndAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime studyStartAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime studyEndAt;

    @Builder
    public StudyGroupUpdateRequest(Long studyGroupId, String subject, String contents, String contentsDetail, int maxSize, Long price, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt, LocalDateTime studyStartAt, LocalDateTime studyEndAt) {
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

    public StudyGroupUpdateServiceRequest toUpdateServiceParam() {
        return StudyGroupUpdateServiceRequest.builder()
                .studyGroupId(this.studyGroupId)
                .subject(this.subject)
                .contents(this.contents)
                .contentsDetail(this.contentsDetail)
                .maxSize(this.maxSize)
                .price(this.price)
                .recruitmentStartAt(this.recruitmentStartAt)
                .recruitmentEndAt(this.recruitmentEndAt)
                .studyStartAt(this.studyStartAt)
                .studyEndAt(this.studyEndAt)
                .build();
    }

}
