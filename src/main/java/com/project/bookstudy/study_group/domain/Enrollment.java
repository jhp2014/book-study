package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id @GeneratedValue
    @Column(name = "enrollment_id")
    private Long id;

    @CreatedDate
    private LocalDateTime enrollDate;

    @Enumerated(EnumType.STRING)
    private EnrollStatus status;

    @Column(name = "enrollment_price")
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    public Enrollment(Long price, Member member, StudyGroup studyGroup, Payment payment) {
        this.price = price;
        this.member = member;
        this.studyGroup = studyGroup;
        this.payment = payment;
        this.status = EnrollStatus.CREATED;
    }

    public void cancel() {
        if (status == EnrollStatus.PAID) { payment.refund();}
        status = EnrollStatus.CANCEL;
    }
}
