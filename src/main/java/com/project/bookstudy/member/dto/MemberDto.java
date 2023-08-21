package com.project.bookstudy.member.dto;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.domain.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberDto {

    private Long id;
    private String email;
    private String name;
    private String phone;

    private String career;
    private Long point;
    private Role role;

    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    @Builder
    private MemberDto(Long id, String email, String name, String phone, String career, Long point, Role role, LocalDateTime registeredAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.career = career;
        this.point = point;
        this.role = role;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
    }

    public static MemberDto from(Member member) {
        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phone(member.getPhone())
                .career(member.getCareer())
                .point(member.getPoint())
                .role(member.getRole())
                .registeredAt(member.getRegisteredAt())
                .updatedAt(member.getUpdatedAt())
                .build();
        return memberDto;
    }
}
