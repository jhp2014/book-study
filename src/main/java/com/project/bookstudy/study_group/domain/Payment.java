package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    private LocalDateTime paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Long price;
    private Long discountPrice;
    private Long paymentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Payment(LocalDateTime paymentDate, Long price, Long discountPrice, Long paymentPrice, Member member) {
        this.paymentDate = paymentDate;
        this.price = price;
        this.discountPrice = discountPrice;
        this.paymentPrice = paymentPrice;
        this.member = member;

        this.status = PaymentStatus.SUCCESS;
    }

    public static Payment createPayment(StudyGroup studyGroup, Member member, LocalDateTime now) {

        //나중에 할인 정책 도입시 할인 금액 계산 로직 작성
        Long price = studyGroup.getPrice();
        member.usePoint(price);

        Payment payment = Payment.builder()
                .member(member)
                .price(price)
                .discountPrice(price)
                .paymentPrice(price)
                .paymentDate(now)
                .build();
        return payment;
    }

    public Long refund() {
        if (status == PaymentStatus.REFUNDED) return 0L;

        this.status = PaymentStatus.REFUNDED;
        return paymentPrice;
    }
}
