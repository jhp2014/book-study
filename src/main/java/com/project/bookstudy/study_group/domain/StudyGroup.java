package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroup {

    @Id @GeneratedValue
    @Column(name = "study_group_id")
    private Long id;
    private String subject;
    private String contents;
    private String contentsDetail;
    private LocalDateTime studyStartAt;
    private LocalDateTime studyEndAt;
    private int maxSize;
    private Long price;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentEndAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Member leader;

    @Builder
    public StudyGroup(String subject, String contents, String contentsDetail, LocalDateTime studyStartAt, LocalDateTime studyEndAt, int maxSize, Long price, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt, Member leader) {
        this.subject = subject;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.maxSize = maxSize;
        this.price = price;
        this.leader = leader;
        this.studyEndAt = studyEndAt;
        this.studyStartAt = studyStartAt;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
    }

    public static StudyGroup from(Member leader, CreateStudyGroupParam studyGroupParam) {
        return StudyGroup.builder()
                .leader(leader)
                .studyStartAt(studyGroupParam.getStudyStartAt())
                .studyEndAt(studyGroupParam.getStudyEndAt())
                .recruitmentStartAt(studyGroupParam.getRecruitmentStartAt())
                .recruitmentEndAt(studyGroupParam.getRecruitmentEndAt())
                .subject(studyGroupParam.getSubject())
                .contents(studyGroupParam.getContents())
                .contentsDetail(studyGroupParam.getContentsDetail())
                .price(studyGroupParam.getPrice())
                .maxSize(studyGroupParam.getMaxSize())
                .build();
    }
}
