package com.project.bookstudy.member.domain;

import lombok.RequiredArgsConstructor;

public enum Role {
    MEMBER("ROLE_MEMBER"), MENTOR("ROLE_MENTOR");

    public final String ROLE_TEXT;
    private Role(String ROLE_TEXT) {
        this.ROLE_TEXT = ROLE_TEXT;
    }
}
