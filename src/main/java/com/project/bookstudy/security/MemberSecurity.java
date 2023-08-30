package com.project.bookstudy.security;

import com.project.bookstudy.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MemberSecurity extends User {
    private final Member member;

    public MemberSecurity(Member member) {
        super(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority("NORMAL")));
        this.member = member;
    }
}
