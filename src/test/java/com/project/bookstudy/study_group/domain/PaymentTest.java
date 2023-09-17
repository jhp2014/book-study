package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {
    @DisplayName("결제 시 결제 상태는 SUCCESS 이다.")
    @Test
    void createPayment1() {
        //given
        Long memberPoint = 50_000L;
        Long studyPrice = 25_000L;

        Member member = Member.builder()
                .name("name")
                .build();
        member.chargePoint(memberPoint);

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .price(studyPrice)
                .build();

        LocalDateTime fixedLocalDateTime = LocalDateTime.of(2023, 1, 6, 0, 0, 0);
        Clock clock = Mockito.mock(Clock.class);
        BDDMockito.given(clock.instant())
                .willReturn(fixedLocalDateTime.toInstant(ZoneOffset.UTC));
        BDDMockito.given(clock.getZone())
                .willReturn(ZoneOffset.systemDefault());

        //when
        Payment payment = Payment.createPayment(studyGroup, member, LocalDateTime.now(clock));

        //then
        assertThat(payment.getPaymentDate()).isEqualTo(LocalDateTime.now(clock));
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @DisplayName("결제 시 회원의 포인트는 스터디 그룹의 금액 만큼 차감 된다.")
    @Test
    void createPaymentSuccess2() {
        //given
        Long memberPoint = 50_000L;
        Long studyPrice = 25_000L;

        Member member = Member.builder()
                .name("name")
                .build();
        member.chargePoint(memberPoint);

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .price(studyPrice)
                .build();

        LocalDateTime fixedLocalDateTime = LocalDateTime.of(2023, 1, 6, 0, 0, 0);
        Clock clock = Mockito.mock(Clock.class);
        BDDMockito.given(clock.instant())
                .willReturn(fixedLocalDateTime.toInstant(ZoneOffset.UTC));
        BDDMockito.given(clock.getZone())
                .willReturn(ZoneOffset.systemDefault());

        //when
        Payment payment = Payment.createPayment(studyGroup, member, LocalDateTime.now(clock));

        //then
        assertThat(member.getPoint()).isEqualTo(memberPoint - studyPrice);
    }


}