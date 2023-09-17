package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id @GeneratedValue
    @Column(name = "enrollment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private EnrollStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;


    @Builder
    private Enrollment(Member member, StudyGroup studyGroup, Payment payment) {
        this.member = member;
        this.studyGroup = studyGroup;
        this.payment = payment;

        this.status = EnrollStatus.RESERVED;

    }

    public static Enrollment of(Member member, StudyGroup studyGroup, LocalDateTime enrollDate) {
        studyGroup.isApplicable();
        Payment payment = Payment.createPayment(studyGroup, member, enrollDate);

        Enrollment enrollment = Enrollment.builder()
                .member(member)
                .studyGroup(studyGroup)
                .payment(payment)
                .build();

        //양방향 연관관계 설정
        studyGroup.getEnrollments().add(enrollment);
        return enrollment;
    }

    public void cancel() {
        if (status == EnrollStatus.CANCEL) return;

        Long refundPrice = payment.refund();
        member.chargePoint(refundPrice);
        status = EnrollStatus.CANCEL;
    }
}
