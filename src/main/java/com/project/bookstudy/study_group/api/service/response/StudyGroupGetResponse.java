package com.project.bookstudy.study_group.api.service.response;

import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.StudyGroupStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class StudyGroupGetResponse {

    private Long id;
    private String subject;
    private String contents;
    private String contentsDetail;
    private LocalDateTime studyStartAt;
    private LocalDateTime studyEndAt;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentEndAt;
    private int maxSize;
    private Long price;
    private MemberDto leader;

    @Builder
    public StudyGroupGetResponse(Long id, String subject, String contents, String contentsDetail, int maxSize, Long price,
                                 LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt,
                                 StudyGroupStatus status, MemberDto leader) {
        this.id = id;
        this.subject = subject;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.studyStartAt = studyStartAt;
        this.studyEndAt = studyEndAt;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
        this.maxSize = maxSize;
        this.price = price;
        this.leader = leader;
    }

    public static StudyGroupGetResponse fromEntity(StudyGroup studyGroup) {
        return StudyGroupGetResponse.builder()
                .id(studyGroup.getId())
                .subject(studyGroup.getSubject())
                .contents(studyGroup.getContents())
                .contentsDetail(studyGroup.getContentsDetail())
                .studyStartAt(studyGroup.getStudyStartAt())
                .studyEndAt(studyGroup.getStudyEndAt())
                .recruitmentStartAt(studyGroup.getRecruitmentStartAt())
                .recruitmentEndAt(studyGroup.getRecruitmentEndAt())
                .maxSize(studyGroup.getMaxSize())
                .price(studyGroup.getPrice())
                .status(studyGroup.getStatus())
                .leader(MemberDto.fromEntity(studyGroup.getLeader()))
                .build();
    }
}
