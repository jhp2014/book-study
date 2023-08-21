package com.project.bookstudy.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateMemberResponse {

    private Long id;
    private String name;

    @Builder
    private CreateMemberResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CreateMemberResponse from(MemberDto memberDto) {
        return CreateMemberResponse.builder()
                .id(memberDto.getId())
                .name(memberDto.getName())
                .build();
    }
}
