package com.project.bookstudy.member.controller;

import com.project.bookstudy.member.Service.MemberService;
import com.project.bookstudy.member.dto.CreateMemberRequest;
import com.project.bookstudy.member.dto.CreateMemberResponse;
import com.project.bookstudy.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    public CreateMemberResponse join(@RequestBody @Valid CreateMemberRequest request) {
        MemberDto memberDto = memberService.joinMember(request);
        return CreateMemberResponse.from(memberDto);
    }

}
