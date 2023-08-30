package com.project.bookstudy.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    @Builder
    public JsonUsernamePasswordAuthenticationFilter(String loginUrl, AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super(loginUrl, authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {


        EmailPasswordRequest emailPasswordRequest
                = objectMapper.readValue(request.getInputStream(), EmailPasswordRequest.class);

        UsernamePasswordAuthenticationToken token
                = UsernamePasswordAuthenticationToken.unauthenticated(emailPasswordRequest.getEmail(), emailPasswordRequest.getPassword());

        log.info("실행! :: JsonUsernamePasswordAuthenticationFilter.attemptAuthentication");

        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(token);
    }

    @Getter
    static class EmailPasswordRequest {
        private String email;
        private String password;

        @Builder
        public EmailPasswordRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
