package com.project.bookstudy.security.filter;

import com.project.bookstudy.security.dto.TokensDto;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.security.filter.utils.ResponseUtils;
import com.project.bookstudy.security.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenRefreshFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!isCorrectUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String refreshToken = request.getHeader(JwtTokenService.REFRESH_TOKEN_REQUEST_HEADER);

        if (!StringUtils.hasText(refreshToken)) {
            throw new IllegalArgumentException(ErrorMessage.INCORRECT_HEADER.getMessage());
        }

        TokensDto tokens = jwtTokenService.reIssueTokens(refreshToken);
        ResponseUtils.writeTokenResponse(response, tokens);
    }

    private boolean isCorrectUrl(HttpServletRequest request) {
        return request.getRequestURI().equals("/refresh");
    }
}
