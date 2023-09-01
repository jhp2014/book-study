package com.project.bookstudy.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.security.ExceptionHandlerFilter;
import com.project.bookstudy.security.JwtAuthenticationFilter;
import com.project.bookstudy.security.oauth.KakaoOAuth2MemberService;
import com.project.bookstudy.security.oauth.OAuth2LoginSuccessHandler;
import com.project.bookstudy.security.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final KakaoOAuth2MemberService kakaoOAuth2MemberService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests().mvcMatchers("/test").authenticated()
                .and()
                .oauth2Login()
                    .userInfoEndpoint().userService(kakaoOAuth2MemberService).and()
                    .successHandler(new OAuth2LoginSuccessHandler(jwtTokenProvider))
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(objectMapper), JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console());
    }
}
