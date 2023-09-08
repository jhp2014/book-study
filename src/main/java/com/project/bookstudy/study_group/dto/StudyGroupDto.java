package com.project.bookstudy.study_group.dto;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.StudyGroupStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class StudyGroupDto {

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
    private Long leaderId;
    private String leaderName;

    @Builder
    public StudyGroupDto(Long id, String subject, String contents, String contentsDetail, int maxSize, Long price,
                         LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt,
                         Long leaderId, StudyGroupStatus status, String leaderName) {
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
        this.leaderId = leaderId;
        this.leaderName = leaderName;
    }

    public static StudyGroupDto fromEntity(StudyGroup studyGroup, Member leader) {
        return StudyGroupDto.builder()
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
                .leaderId(leader.getId())
                .leaderName(leader.getName())
                .build();
    }
}
