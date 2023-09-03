package com.project.bookstudy.security.filter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.dto.ErrorResponse;
import com.project.bookstudy.security.dto.TokensDto;
import com.project.bookstudy.security.service.JwtTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseUtils {

    public static void write401Error(HttpServletResponse response, ObjectMapper objectMapper, String message) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .build();

        response.setContentType(MediaType.APPLICATION_JSON.getType());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    public static void writeTokenResponse(HttpServletResponse response, TokensDto tokensDto) {
        response.setContentType(MediaType.APPLICATION_JSON.getType());
        response.setStatus(HttpStatus.OK.value());
        response.setHeader(JwtTokenService.ACCESS_TOKEN_RESPONSE_HEADER, tokensDto.getAccessToken());
        response.setHeader(JwtTokenService.REFRESH_TOKEN_RESPONSE_HEADER, tokensDto.getRefreshToken());
    }
}
