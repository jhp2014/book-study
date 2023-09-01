package com.project.bookstudy.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.security.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberOAuth2User oAuth2User = (MemberOAuth2User) authentication.getPrincipal();

        Member member = oAuth2User.getMember();

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        //응답 본문에 토큰 값
        response.setHeader(JwtTokenProvider.ACCESS_TOKEN_RESPONSE_HEADER, accessToken);
        response.setHeader(JwtTokenProvider.REFRESH_TOKEN_RESPONSE_HEADER, refreshToken);
    }
}
