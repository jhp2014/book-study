package com.project.bookstudy.study_group.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
public class UpdateStudyGroupRequest {

    private Long id;
    private int maxSize;
    private String contents;
    private String subject;
    private Long price;
    private String contentsDetail;

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
    public UpdateStudyGroupRequest(Long id, int maxSize, String contents, String subject, Long price, String contentsDetail, LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentEndAt, LocalDateTime recruitmentStartAt) {
        this.id = id;
        this.maxSize = maxSize;
        this.contents = contents;
        this.subject = subject;
        this.price = price;
        this.contentsDetail = contentsDetail;
        this.studyStartAt = studyStartAt;
        this.studyEndAt = studyEndAt;
        this.recruitmentEndAt = recruitmentEndAt;
        this.recruitmentStartAt = recruitmentStartAt;
    }

    public UpdateStudyGroupParam getUpdateStudyGroupParam() {
        return UpdateStudyGroupParam.builder()
                .id(id)
                .maxSize(maxSize)
                .contents(contents)
                .subject(subject)
                .price(price)
                .contentsDetail(contentsDetail)
                .studyStartAt(studyStartAt)
                .studyEndAt(studyEndAt)
                .recruitmentStartAt(recruitmentStartAt)
                .recruitmentEndAt(recruitmentEndAt)
                .build();
    }
}
