package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
class StudyGroupServiceTest {

    @Autowired
    EntityManager entityManager;
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
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");

        //when
        StudyGroupDto studyGroupDto = studyGroupService.createStudyGroup(request.getMemberId(), request.getCreateStudyGroupParam());

        entityManager.flush();
        entityManager.clear();

        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));
        Class<? extends Member> aClass = studyGroup.getLeader().getClass();
        System.out.println(aClass.getName());


        //then
        Assertions.assertThat(studyGroup.getId()).isEqualTo(studyGroupDto.getId());

        Assertions.assertThat(studyGroup.getStudyStartAt()).isNotNull();
        Assertions.assertThat(studyGroup.getStudyEndAt()).isNotNull();
        Assertions.assertThat(studyGroup.getRecruitmentStartAt()).isNotNull();
        Assertions.assertThat(studyGroup.getRecruitmentEndAt()).isNotNull();

        Assertions.assertThat(studyGroup.getSubject()).isEqualTo(request.getSubject());
        Assertions.assertThat(studyGroup.getContents()).isEqualTo(request.getContents());
        Assertions.assertThat(studyGroup.getContentsDetail()).isEqualTo(request.getContentsDetail());
        Assertions.assertThat(studyGroup.getPrice()).isEqualTo(request.getPrice());
        Assertions.assertThat(studyGroup.getMaxSize()).isEqualTo(request.getMaxSize());
        Assertions.assertThat(studyGroup.getLeader().getId()).isSameAs(member.getId());
    }

    @Test
    @DisplayName("StudyGroup 단건 조회")
    @Transactional
    void getStudySuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제").getCreateStudyGroupParam();
        StudyGroup saveStudyGroup = studyGroupRepository.save(StudyGroup.from(member, param));

        entityManager.flush();

        //when
        StudyGroupDto findStudyGroup = studyGroupService.getStudyGroup(saveStudyGroup.getId());

        //then
        Assertions.assertThat(findStudyGroup.getLeaderName()).isEqualTo(member.getName());
        Assertions.assertThat(findStudyGroup.getId()).isEqualTo(saveStudyGroup.getId());
        Assertions.assertThat(findStudyGroup.getPrice()).isEqualTo(param.getPrice());
        Assertions.assertThat(findStudyGroup.getContents()).isEqualTo(param.getContents());
        Assertions.assertThat(findStudyGroup.getContentsDetail()).isEqualTo(param.getContentsDetail());
        Assertions.assertThat(findStudyGroup.getMaxSize()).isEqualTo(param.getMaxSize());
    }

    @Test
    @DisplayName("StudyGroup 여러건 조회")
    @Transactional
    void getStudyListSuccess() {
        //given
        int totalDataNum = 50;
        List<StudyGroup> studyGroups = new ArrayList<>();
        IntStream.range(0,totalDataNum).forEach((i) -> {
            Member member = memberRepository.save(createMember("박종훈" + i));
            CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제" + i).getCreateStudyGroupParam();
            StudyGroup savedGroup = studyGroupRepository.save(StudyGroup.from(member, param));
            studyGroups.add(savedGroup);
        });

        entityManager.flush();

        //when
        int pageSize = 10;
        Page<StudyGroupDto> studyGroupList = studyGroupService.getStudyGroupList(PageRequest.of(2, pageSize));

        //then
        List<StudyGroupDto> content = studyGroupList.getContent();

        Assertions.assertThat(studyGroupList.getTotalElements()).isEqualTo((long) totalDataNum);
        Assertions.assertThat(content.size()).isEqualTo(pageSize);
    }

    @Test
    @DisplayName("StudyGroup 수정")
    @Transactional
    void updateStudySuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제").getCreateStudyGroupParam();
        StudyGroup saveStudyGroup = studyGroupRepository.save(StudyGroup.from(member, param));

        UpdateStudyGroupParam updateParam = getUpdateStudyGroupRequest("update_subject").getUpdateStudyGroupParam();

        //when
        studyGroupService.updateStudyGroup(saveStudyGroup.getId(), updateParam);
        entityManager.flush();

        //then
        StudyGroup findStudyGroup = studyGroupRepository.findById(saveStudyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Assertions.assertThat(findStudyGroup.getSubject()).isEqualTo(updateParam.getSubject());
        Assertions.assertThat(findStudyGroup.getPrice()).isEqualTo(updateParam.getPrice());
        Assertions.assertThat(findStudyGroup.getContents()).isEqualTo(updateParam.getContents());
        Assertions.assertThat(findStudyGroup.getContentsDetail()).isEqualTo(updateParam.getContentsDetail());
        Assertions.assertThat(findStudyGroup.getMaxSize()).isEqualTo(updateParam.getMaxSize());
    }


    private Member createMember(String name) {
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

    private CreateStudyGroupRequest getStudyGroupRequest(Member member, String subject) {
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

    private UpdateStudyGroupRequest getUpdateStudyGroupRequest(String subject) {

        String contents = "update_contests";
        String contestsDetail = "update_detail";
        Long price = 500000L;
        int maxSize = 50;

        LocalDateTime recruitStart = LocalDateTime.now();
        LocalDateTime recruitEnd = recruitStart.plusDays(3);
        LocalDateTime start = recruitEnd.plusDays(3);
        LocalDateTime end = start.plusDays(3);

        UpdateStudyGroupRequest request = UpdateStudyGroupRequest.builder()
                .maxSize(maxSize)
                .contents(contents)
                .subject(subject)
                .price(price)
                .contentsDetail(contestsDetail)
                .studyStartAt(start)
                .studyEndAt(end)
                .recruitmentStartAt(recruitStart)
                .recruitmentEndAt(recruitEnd)
                .build();

        return request;
    }

}