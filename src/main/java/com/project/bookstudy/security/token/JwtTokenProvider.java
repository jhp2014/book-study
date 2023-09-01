package com.project.bookstudy.security.token;

import com.project.bookstudy.member.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    public static final String ACCESS_TOKEN_RESPONSE_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_RESPONSE_HEADER = "Authorization-Refresh";
    private Key key;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public static final String MEMBER_ID = "memberId";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 1; // 30min
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 14days

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

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        readToken(token);
    }

    private Jws<Claims> readToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public Authentication getAuthentication(String token) {
        Long memberId = getMemberId(token);
        return new UsernamePasswordAuthenticationToken(memberId, ""
                ,List.of(new SimpleGrantedAuthority(Role.MEMBER.ROLE_TEXT)));
    }

    private Long getMemberId(String token) {
        return readToken(token).getBody().get(MEMBER_ID, Long.class);
    }
}
