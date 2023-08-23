package com.project.bookstudy.member.Service;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.domain.MemberStatus;
import com.project.bookstudy.member.domain.Role;
import com.project.bookstudy.member.dto.CreateMemberRequest;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.member.exception.DuplicateEmail;
import com.project.bookstudy.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 테스트")
    @Transactional
    void successJoinMember() {
        //given
        String name = "name";
        String password = "!@!@fjaweio31";
        String phone = "010-7474-8181";
        String email = "whdgnsdl35@naver.com";
        String career = "취준생";

        CreateMemberRequest request = CreateMemberRequest.builder()
            .name(name)
            .password(password)
            .phone(phone)
            .email(email)
            .career(career)
            .build();

        //when
        MemberDto memberDto = memberService.joinMember(request);

        //then
        Member findMember = memberRepository.findById(memberDto.getId()).get();

        Assertions.assertThat(findMember.getName()).isEqualTo(request.getName());
        Assertions.assertThat(findMember.getEmail()).isEqualTo(request.getEmail());
        Assertions.assertThat(passwordEncoder.matches(request.getPassword(), findMember.getPassword())).isTrue();
        Assertions.assertThat(findMember.getCareer()).isEqualTo(request.getCareer());
        Assertions.assertThat(findMember.getPhone()).isEqualTo(request.getPhone());
        Assertions.assertThat(findMember.getPoint()).isEqualTo(0L);
        Assertions.assertThat(findMember.getRole()).isEqualTo(Role.MEMBER);
        Assertions.assertThat(findMember.getRegisteredAt()).isNotNull();
        Assertions.assertThat(findMember.getUpdatedAt()).isNotNull();
        Assertions.assertThat(findMember.getStatus()).isEqualTo(MemberStatus.WAITING);
    }

    @Test
    @DisplayName("이메일 중복으로 인한 회원가입 실패 ")
    @Transactional
    void failJoinMember() {
        //given
        String name = "name";
        String password = "!@fdsajkwh2321";
        String phone = "010-7474-8181";
        String email = "whdgnsdl35@naver.com";
        String career = "취준생";

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .name(name)
                .career(career)
                .build();

        memberRepository.save(member);

        CreateMemberRequest request = CreateMemberRequest.builder()
                .name(name)
                .password(password)
                .phone(phone)
                .email(email)
                .career(career)
                .build();

        //expected
        Assertions.assertThatThrownBy(() -> memberService.joinMember(request))
                .isInstanceOf(DuplicateEmail.class);
    }
}