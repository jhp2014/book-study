package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.*;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
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

import static org.assertj.core.api.Assertions.*;

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

    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private EnrollmentRepository enrollmentRepository;


    @Test
    @DisplayName("StudyGroup 생성 성공")
    @Transactional
    void createStudySuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");

        //when
        StudyGroupDto studyGroupDto = studyGroupService.createStudyGroup(request.getMemberId(), request.toStudyGroupParam());

        entityManager.flush();
        entityManager.clear();

        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));
        Class<? extends Member> aClass = studyGroup.getLeader().getClass();
        System.out.println(aClass.getName());


        //then
        assertThat(studyGroup.getId()).isEqualTo(studyGroupDto.getId());

        assertThat(studyGroup.getStudyStartAt()).isNotNull();
        assertThat(studyGroup.getStudyEndAt()).isNotNull();
        assertThat(studyGroup.getRecruitmentStartAt()).isNotNull();
        assertThat(studyGroup.getRecruitmentEndAt()).isNotNull();

        assertThat(studyGroup.getSubject()).isEqualTo(request.getSubject());
        assertThat(studyGroup.getContents()).isEqualTo(request.getContents());
        assertThat(studyGroup.getContentsDetail()).isEqualTo(request.getContentsDetail());
        assertThat(studyGroup.getPrice()).isEqualTo(request.getPrice());
        assertThat(studyGroup.getMaxSize()).isEqualTo(request.getMaxSize());
        assertThat(studyGroup.getLeader().getId()).isSameAs(member.getId());
    }

    @Test
    @DisplayName("StudyGroup 단건 조회")
    @Transactional
    void getStudySuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제").toStudyGroupParam();
        StudyGroup saveStudyGroup = studyGroupRepository.save(StudyGroup.from(member, param));

        entityManager.flush();

        //when
        StudyGroupDto findStudyGroup = studyGroupService.getStudyGroup(saveStudyGroup.getId());

        //then
        assertThat(findStudyGroup.getLeaderName()).isEqualTo(member.getName());
        assertThat(findStudyGroup.getId()).isEqualTo(saveStudyGroup.getId());
        assertThat(findStudyGroup.getPrice()).isEqualTo(param.getPrice());
        assertThat(findStudyGroup.getContents()).isEqualTo(param.getContents());
        assertThat(findStudyGroup.getContentsDetail()).isEqualTo(param.getContentsDetail());
        assertThat(findStudyGroup.getMaxSize()).isEqualTo(param.getMaxSize());
    }

    @Test
    @DisplayName("StudyGroup 여러건 조회")
    @Transactional
    void getStudyListSuccess() {
        //given
        int totalDataNum = 10;
        List<StudyGroup> studyGroups = new ArrayList<>();
        IntStream.range(0,totalDataNum).forEach((i) -> {
            Member member = memberRepository.save(createMember("박종훈" + i));
            CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제" + i).toStudyGroupParam();
            StudyGroup savedGroup = studyGroupRepository.save(StudyGroup.from(member, param));
            studyGroups.add(savedGroup);
        });

        entityManager.flush();

        //when
        int pageSize = 5;
        StudyGroupSearchCond cond = StudyGroupSearchCond.builder()
                .leaderName("4")
                .build();

        Page<StudyGroupDto> studyGroupList = studyGroupService.getStudyGroupList(PageRequest.of(0, pageSize), cond);

        //then
        List<StudyGroupDto> content = studyGroupList.getContent();

        assertThat(studyGroupList.getTotalElements()).isEqualTo((long) totalDataNum);
    }

    @Test
    @DisplayName("StudyGroup soft 삭제")
    @Transactional
    void deleteStudySuccess() {
        //given
        Member member1 = memberRepository.save(createMember("박종훈1"));
        Member member2 = memberRepository.save(createMember("박종훈2"));
        member2.chargePoint(50000L);

        CreateStudyGroupParam param = getStudyGroupRequest(member1, "스터디 주제").toStudyGroupParam();
        StudyGroup saveStudyGroup = studyGroupRepository.save(StudyGroup.from(member1, param));

        CreateEnrollmentRequest request = CreateEnrollmentRequest.builder()
                .studyGroupId(saveStudyGroup.getId())
                .memberId(member2.getId()).build();

        Long enrollmentId = enrollmentService.enroll(request);
        entityManager.flush();

        //when
        log.info(">>>>>>>>>>> WHEN FLUSH");
        studyGroupService.cancelStudyGroup(saveStudyGroup.getId());
        entityManager.flush();

        //then
        Enrollment enrollment = enrollmentRepository.findByIdWithAll(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(saveStudyGroup.getStatus()).isEqualTo(StudyGroupStatus.CANCEL);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollStatus.CANCEL);
        assertThat(enrollment.getPayment().getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(enrollment.getMember().getPoint()).isEqualTo(member2.getPoint());

    }

    @Test
    @DisplayName("StudyGroup 수정")
    @Transactional
    void updateStudySuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제").toStudyGroupParam();
        StudyGroup saveStudyGroup = studyGroupRepository.save(StudyGroup.from(member, param));

        UpdateStudyGroupParam updateParam = getUpdateStudyGroupRequest("update_subject", saveStudyGroup.getId()).getUpdateStudyGroupParam();

        //when
        studyGroupService.updateStudyGroup(updateParam);
        entityManager.flush();

        //then
        StudyGroup findStudyGroup = studyGroupRepository.findById(saveStudyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findStudyGroup.getSubject()).isEqualTo(updateParam.getSubject());
        assertThat(findStudyGroup.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findStudyGroup.getContents()).isEqualTo(updateParam.getContents());
        assertThat(findStudyGroup.getContentsDetail()).isEqualTo(updateParam.getContentsDetail());
        assertThat(findStudyGroup.getMaxSize()).isEqualTo(updateParam.getMaxSize());
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
        Long price = 1234L;
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

    private UpdateStudyGroupRequest getUpdateStudyGroupRequest(String subject, Long id) {

        String contents = "update_contests";
        String contestsDetail = "update_detail";
        Long price = 100030L;
        int maxSize = 50;

        LocalDateTime recruitStart = LocalDateTime.now();
        LocalDateTime recruitEnd = recruitStart.plusDays(3);
        LocalDateTime start = recruitEnd.plusDays(3);
        LocalDateTime end = start.plusDays(3);

        UpdateStudyGroupRequest request = UpdateStudyGroupRequest.builder()
                .id(id)
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