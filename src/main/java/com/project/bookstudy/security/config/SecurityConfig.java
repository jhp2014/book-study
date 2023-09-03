package com.project.bookstudy.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.domain.Role;
import com.project.bookstudy.security.filter.JwtTokenRefreshFilter;
import com.project.bookstudy.security.filter.hadler.ApiAccessDeniedHandler;
import com.project.bookstudy.security.filter.hadler.ApiAuthenticationEntryPoint;
import com.project.bookstudy.security.filter.ExceptionHandlerFilter;
import com.project.bookstudy.security.filter.JwtAuthenticationFilter;
import com.project.bookstudy.security.service.KakaoOAuth2MemberService;
import com.project.bookstudy.security.filter.hadler.OAuth2LoginFailureHandler;
import com.project.bookstudy.security.filter.hadler.OAuth2LoginSuccessHandler;
import com.project.bookstudy.security.service.JwtTokenService;
import com.project.bookstudy.security.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;
    private final KakaoOAuth2MemberService kakaoOAuth2MemberService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .headers().frameOptions().disable();

        http.authorizeRequests()
                .mvcMatchers("/test").authenticated()
                .anyRequest().permitAll();

        http.oauth2Login()
                .userInfoEndpoint().userService(kakaoOAuth2MemberService).and()
                .successHandler(new OAuth2LoginSuccessHandler(jwtTokenService, tokenRepository))
                .failureHandler(new OAuth2LoginFailureHandler(objectMapper));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenRefreshFilter(jwtTokenService), JwtAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(objectMapper), JwtTokenRefreshFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(new ApiAccessDeniedHandler(objectMapper))    //AccessDeniedException
                .authenticationEntryPoint(new ApiAuthenticationEntryPoint(objectMapper)); //AuthenticationException

        return http.build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console());
    }
}
