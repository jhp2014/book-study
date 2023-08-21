package com.project.bookstudy.member.Service;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.dto.CreateMemberRequest;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.member.exception.DuplicateEmail;
import com.project.bookstudy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberDto joinMember(CreateMemberRequest request) {

        validateDuplicateEmail(request.getEmail());

        Member member = Member.builder()
                .name(request.getName())
                .password(request.getPassword())    //비밀번호 암호화 후 저장해야한다.
                .email(request.getEmail())
                .career(request.getCareer())
                .phone(request.getPhone())
                .build();

        Member savedMember = memberRepository.save(member);

        return MemberDto.from(savedMember);
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmail("email", "같은 이메일이 존재합니다.");
        }
    }
}
