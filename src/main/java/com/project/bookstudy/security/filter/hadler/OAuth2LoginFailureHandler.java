package com.project.bookstudy.security.filter.hadler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.security.filter.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseUtils.write401Error(response, objectMapper, exception.getMessage());
        log.error(">>>>> error ", exception);
    }
}
