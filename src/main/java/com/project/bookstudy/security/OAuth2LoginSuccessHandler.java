package com.project.bookstudy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.exception.MemberNotFound;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.security.token.JwtTokenDto;
import com.project.bookstudy.security.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User memberInfo = (OAuth2User) authentication.getPrincipal();

        String email = (String) ((Map) memberInfo.getAttribute("kakao_account")).get("email");

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFound());

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        //응답 본문에 토큰 값
        response.getWriter().write(objectMapper.writeValueAsString(JwtTokenDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()));
    }

}
