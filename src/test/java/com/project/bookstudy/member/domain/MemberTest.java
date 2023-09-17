package com.project.bookstudy.member.domain;

import com.project.bookstudy.common.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @DisplayName("회원은 보유 포인트보다 많은 포인트를 사용할 수 없다.")
    @Test
    void usePointFail() {
        //given
        Long initPoint = 50_000L;
        Long pointUsage = 100_000L;

        Member member = Member.builder()
                .email("email")
                .name("name")
                .phone("phone")
                .career("career")
                .build();
        member.chargePoint(initPoint);

        //when //then
        assertThatThrownBy(() -> member.usePoint(pointUsage))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.NOT_ENOUGH_POINT.getMessage());
    }

}