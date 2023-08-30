package com.project.bookstudy.member.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

public class MemberEditDto {

    private String name;
    private String phone;

    private String career;
    @Enumerated(EnumType.STRING)
    private Role role;
}
