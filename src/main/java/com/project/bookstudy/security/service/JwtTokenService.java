package com.project.bookstudy.security.service;

import com.project.bookstudy.member.domain.Role;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.security.domain.RefreshToken;
import com.project.bookstudy.security.dto.TokensDto;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.security.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JwtTokenService {

    public static final String ACCESS_TOKEN_REQUEST_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_REQUEST_HEADER = "RefreshToken";
    public static final String ACCESS_TOKEN_RESPONSE_HEADER = "AccessToken";
    public static final String REFRESH_TOKEN_RESPONSE_HEADER = "RefreshToken";
    public static final String ACCESS_TOKEN_PREFIX = "Bearer ";
    public static final String MEMBER_ID = "memberId";
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30min
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 14days

    private Key key;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public JwtTokenService(@Value("${jwt.secret}") String secretKey,
                           TokenRepository tokenRepository,
                           MemberRepository memberRepository) {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public TokensDto reIssueTokens(String refreshTokenValue) {

        RefreshToken refreshToken = tokenRepository.findByTokenValue(refreshTokenValue)
                .orElseThrow(() -> new MalformedJwtException(ErrorMessage.INVALID_TOKEN.getMessage()));
        TokensDto tokens = createTokens(refreshToken.getMemberId());
        return tokens;
    }

    public TokensDto createTokens(Long memberId) {
        return TokensDto.builder()
                .accessToken(createAccessToken(memberId))
                .refreshToken(createRefreshToken(memberId))
                .build();
    }

    public String createAccessToken(Long memberId) {
        Claims claims = Jwts.claims();
        claims.put(MEMBER_ID, memberId);

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long memberId) {

        Claims claims = Jwts.claims();
        claims.put(MEMBER_ID, memberId);

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String tokenValue = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        saveOrUpdateRefreshToken(memberId, tokenValue);

        return tokenValue;
    }

    private void saveOrUpdateRefreshToken(Long memberId, String tokenValue) {
        Optional<RefreshToken> optionalRefreshToken = tokenRepository.findByMemberId(memberId);

        if (optionalRefreshToken.isEmpty()) {
            tokenRepository.save(RefreshToken.builder()
                    .memberId(memberId)
                    .token(tokenValue)
                    .build());
            return;
        }

        RefreshToken refreshToken = optionalRefreshToken.get();
        refreshToken.updateTokenValue(tokenValue);
    }


    public Authentication getAuthentication(String token) {
        Long memberId = getMemberId(token);
        return new UsernamePasswordAuthenticationToken(memberId, ""
                ,List.of(new SimpleGrantedAuthority(Role.MEMBER.ROLE_TEXT)));
    }

    private Long getMemberId(String token) {
        return readToken(parseToken(token)).getBody().get(MEMBER_ID, Long.class);
    }
    private String parseToken(String token) {
        return token.substring(ACCESS_TOKEN_PREFIX.length());
    }

    private Jws<Claims> readToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
