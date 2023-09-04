package com.project.bookstudy.study_group.service;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
class StudyGroupServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private StudyGroupService studyGroupService;
    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Test
    @DisplayName("StudyGroup 생성 성공")
    @Transactional
    void createStudySuccess() {
        //given
        Member member = memberRepository.save(createMember());
        CreateStudyGroupRequest request = getStudyGroupRequest(member);

        //when
        StudyGroupDto studyGroupDto = studyGroupService.createStudyGroup(request.createStudyGroupParam(), request.getMemberId());

        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        //then
        Assertions.assertThat(studyGroup.getId()).isEqualTo(studyGroupDto.getId());

        Assertions.assertThat(studyGroup.getStudyStartAt()).isNotNull();
        Assertions.assertThat(studyGroup.getStudyEndAt()).isNotNull();
        Assertions.assertThat(studyGroup.getRecruitmentStartAt()).isNotNull();
        Assertions.assertThat(studyGroup.getRecruitmentEndAt()).isNotNull();

        Assertions.assertThat(studyGroup.getSubject()).isEqualTo(studyGroupDto.getSubject());
        Assertions.assertThat(studyGroup.getContents()).isEqualTo(studyGroupDto.getContents());
        Assertions.assertThat(studyGroup.getContentsDetail()).isEqualTo(studyGroupDto.getContentsDetail());
        Assertions.assertThat(studyGroup.getPrice()).isEqualTo(studyGroupDto.getPrice());
        Assertions.assertThat(studyGroup.getMaxSize()).isEqualTo(studyGroupDto.getMaxSize());

        Member leader = studyGroup.getLeader();
        Assertions.assertThat(leader.getId()).isSameAs(member.getId());
    }

    private Member createMember() {
        String name = "박종훈";
        String career = "career";
        String phone = "010-5453-5325";
        String email = "whdgnsdl35@gmail.com";
        return Member.builder()
                .name(name)
                .career(career)
                .phone(phone)
                .email(email)
                .build();
    }

    private CreateStudyGroupRequest getStudyGroupRequest(Member member) {
        String subject = "test";
        String contents = "test_contests";
        String contestsDetail = "test_detail";
        Long price = 100000L;
        int maxSize = 10;

        LocalDateTime recruitStart = LocalDateTime.now();
        LocalDateTime recruitEnd = recruitStart.plusDays(3);
        LocalDateTime start = recruitEnd.plusDays(3);
        LocalDateTime end = start.plusDays(3);

        CreateStudyGroupRequest request = CreateStudyGroupRequest.builder()
                .memberId(member.getId())
                .recruitmentStartAt(recruitStart)
                .recruitmentEndAt(recruitEnd)
                .studyStartAt(start)
                .studyEndAt(end)
                .subject(subject)
                .contents(contents)
                .contentsDetail(contestsDetail)
                .price(price)
                .maxSize(maxSize)
                .build();
        return request;
    }

}