package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Payment {

    @Id @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    private LocalDateTime paymentDate;
    private PaymentStatus status;

    private Long price;
    private Long discountPrice;
    private Long paymentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void refund() {
        if (status != PaymentStatus.SUCCESS) throw new IllegalStateException(ErrorMessage.REFUND_FAIL.getMessage());
        member.chargePoint(paymentPrice);
        this.status = PaymentStatus.REFUNDED;
    }
}
