package com.project.bookstudy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessage {

    SOCIAL_LOGIN_FAIL("소셜 로그인 실패"),
    NO_AUTHORITY("권한 없음"),
    LOGIN_NEED("로그인 필요"),
    INVALID_TOKEN("유효하지 않은 토큰"),
    INCORRECT_HEADER("올바르지 않은 헤더"),
    NO_ENTITY("해당 값의 Entity 없음"),
    STUDY_CANCEL_FAIL("스터디 그룹이 시작해서 취소할 수 없습니다."),
    REFUND_FAIL("환불 실패"),
    NOT_ENOUGH_POINT("포인트 부족"),
    STUDY_FULL("현재 스터디 인원이 가득 찼습니다."),
    INVALID_DATE_RANGE_ERROR("시작 날짜는 종료 날짜 보다 빨랴야 합니다."),
    INVALID_APPLY("시작 날짜는 종료 날짜 보다 빨랴야 합니다."),
    STUDY_LEADER_APPLICATION_ERROR("스터디 리더는 자신의 스터디에 신청할 수 없습니다."),
    DUPLICATE_APPLICATION_ERROR("이미 신청한 스터디 입니다."),
    CHARGE_FAIL("포인트 충전에 실패했습니다.")
    ;
    private String message;
}
