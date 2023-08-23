package com.project.bookstudy.member.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String name;
    private String password;
    private String phone;

    @Lob
    private String career;
    private Long point = 0L; //나중에 Point class 따로 생성?
    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    private Member(String email, String name, String password, String phone, String career) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.career = career;

        this.point = 0L;
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = MemberStatus.WAITING;
        this.role = Role.MEMBER;


    }
}
