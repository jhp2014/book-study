package com.project.bookstudy.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.security.filter.utils.ResponseUtils;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//Filter 에서 올라오는 예외 처리 담당,

@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            ResponseUtils.write401Error(response, objectMapper, e.getMessage());
            log.error("JwtException 발생", e);
        } catch (IllegalArgumentException e) {
            ResponseUtils.write401Error(response, objectMapper, e.getMessage());
            log.error("IllegalArgumentException 발생", e);
        }
    }
}
