package com.project.bookstudy.member.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;
    @Column(nullable = false)
    private String email;
    private String name;
    private String phone;
    @Lob
    private String career;
    private Long point = 0L; //나중에 Point class 따로 생성?
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    private Member(String email, String name, String phone, String career) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.career = career;
        this.point = 0L;
        this.status = MemberStatus.ACTIVE;
        this.role = Role.MEMBER;
    }

    public void updateName(String name) {
        if (name == null) return;
        this.name = name;
    }
}
