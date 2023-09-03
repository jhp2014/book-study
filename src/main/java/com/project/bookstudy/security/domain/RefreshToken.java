package com.project.bookstudy.security.domain;

import com.project.bookstudy.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshToken {

    @Id @GeneratedValue
    private Long tokenId;

    private String tokenValue;

    private Long memberId;

    @Builder
    public RefreshToken(String token, Long memberId) {
        this.tokenValue = token;
        this.memberId = memberId;
    }

    public void updateTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
