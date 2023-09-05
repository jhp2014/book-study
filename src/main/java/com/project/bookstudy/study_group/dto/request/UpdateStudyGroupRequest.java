package com.project.bookstudy.study_group.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
public class UpdateStudyGroupRequest {

//    private String subject; 주제 변경 불가능
//    private Long price; 금액 설정 불가능

    private int maxSize;
    private String contents;
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
    public UpdateStudyGroupRequest(int maxSize, String contents, String contentsDetail, LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentEndAt, LocalDateTime recruitmentStartAt) {
        this.maxSize = maxSize;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.studyStartAt = studyStartAt;
        this.studyEndAt = studyEndAt;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
    }

    public CreateStudyGroupParam getUpdateStudyGroupParam() {
        return CreateStudyGroupParam.builder()
                .maxSize(maxSize)
                .contents(contents)
                .contentsDetail(contentsDetail)
                .studyStartAt(studyStartAt)
                .studyEndAt(studyEndAt)
                .recruitmentStartAt(recruitmentStartAt)
                .recruitmentEndAt(recruitmentEndAt)
                .build();
    }
}
