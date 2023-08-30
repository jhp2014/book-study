package com.project.bookstudy.security.token;


import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenDto {

    private final String accessToken;
    private final String refreshToken;

    @Builder
    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
