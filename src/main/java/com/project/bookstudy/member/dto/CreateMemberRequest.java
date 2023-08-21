package com.project.bookstudy.member.dto;


import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class CreateMemberRequest {

    @Email(message = "올바른 이메일을 입력해 주세요.")
    private String email;
    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "비밀번호는 영어, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.")
    private String password;
    @NotBlank(message = "핸드폰 번호를 입력해 주세요.")
    private String phone;

    private String career;

    @Builder
    private CreateMemberRequest(String email, String name, String password, String phone, String career) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.career = career;
    }
}
