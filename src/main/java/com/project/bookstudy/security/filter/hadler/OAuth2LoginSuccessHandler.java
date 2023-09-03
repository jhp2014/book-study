package com.project.bookstudy.security.filter.hadler;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.security.dto.MemberOAuth2User;
import com.project.bookstudy.security.dto.TokensDto;
import com.project.bookstudy.security.filter.utils.ResponseUtils;
import com.project.bookstudy.security.service.JwtTokenService;
import com.project.bookstudy.security.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;
    private final TokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberOAuth2User oAuth2User = (MemberOAuth2User) authentication.getPrincipal();

        Member member = oAuth2User.getMember();
        TokensDto tokens = jwtTokenService.createTokens(member.getId());

        ResponseUtils.writeTokenResponse(response, tokens);
    }
}
