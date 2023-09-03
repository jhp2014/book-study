package com.project.bookstudy.security.filter.hadler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.security.filter.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseUtils.write401Error(response,objectMapper, accessDeniedException.getMessage());
        log.error("ApiAccessDeniedHandler.handle: ", accessDeniedException);
    }
}
