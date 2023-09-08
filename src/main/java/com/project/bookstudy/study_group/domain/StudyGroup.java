package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"enrollments"})
@SQLDelete(sql = "UPDATE study_group SET is_deleted = true WHERE study_group_id = ?")
public class StudyGroup {

    @Id
    @GeneratedValue
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

    private StudyGroupStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Member leader;

    //동시셩 이슈 발생할 수 있다. → Redis 적용을 통해 해결할 예정
    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments = new ArrayList<>();


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

        this.status = StudyGroupStatus.RECRUITING;
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

    public boolean isApplicable() {

        long count = enrollments.stream()
                .filter((i) -> i.getStatus() == EnrollStatus.RESERVED)
                .count();

        if ( count < maxSize) return true;
        return false;
    }

    public void update(UpdateStudyGroupParam param) {
        this.subject = param.getSubject();
        this.contents = param.getContents();
        this.contentsDetail = param.getContentsDetail();
        this.maxSize = param.getMaxSize();
        this.price = param.getPrice();
        this.studyStartAt = param.getStudyStartAt();
        this.studyEndAt = param.getStudyEndAt();
        this.recruitmentStartAt = param.getRecruitmentStartAt();
        this.recruitmentEndAt = param.getRecruitmentEndAt();
    }

    public void cancel() {
        if (isStarted()) throw new IllegalStateException(ErrorMessage.STUDY_CANCEL_FAIL.getMessage());

        enrollments.stream()
                .forEach(e -> e.cancel());

        status = StudyGroupStatus.CANCEL;
    }

    public boolean isStarted() {
        if (LocalDateTime.now().isAfter(studyStartAt)) return true;
        return false;
    }

}
