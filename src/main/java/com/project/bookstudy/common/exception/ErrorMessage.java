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
    NO_ENTITY("해당 값의 Entity 없음");

    private String message;
}
