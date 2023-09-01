package com.project.bookstudy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


//Filter 에서 올라오는 예외 처리 담당,
//주로 JwtAuthenticationFilter 예외 처리

@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("만료된 토큰입니다.")
                    .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                    .build();

            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

            log.error("ExpiredJwtException 발생", e);
        } catch (JwtException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("올바르지 않은 접근입니다.")
                    .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                    .build();

            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

            log.error("JwtException 발생", e);
        }
    }
}
