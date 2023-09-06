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
    REFUND_FAIL("환불 실패");

    private String message;
}
