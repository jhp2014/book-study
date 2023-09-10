package com.project.bookstudy.study_group.service;

import com.project.bookstudy.TestDataProvider;
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
    private EnrollmentRepository enrollmentRepository;


    @Test
    @Transactional
    void 스터디그룹_생성_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");

        //when
        StudyGroupDto studyGroupDto = studyGroupService.createStudyGroup(request.getMemberId(), request.toStudyGroupParam());

        //then
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

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
    @Transactional
    void 스터디그룹_단건조회_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        //when
        StudyGroupDto findStudyGroup = studyGroupService.getStudyGroup(studyGroup.getId());

        //then
        assertThat(findStudyGroup.getLeaderName()).isEqualTo(member.getName());
        assertThat(findStudyGroup.getId()).isEqualTo(studyGroup.getId());
        assertThat(findStudyGroup.getPrice()).isEqualTo(request.getPrice());
        assertThat(findStudyGroup.getContents()).isEqualTo(request.getContents());
        assertThat(findStudyGroup.getContentsDetail()).isEqualTo(request.getContentsDetail());
        assertThat(findStudyGroup.getMaxSize()).isEqualTo(request.getMaxSize());
    }

    @Test
    @Transactional
    void 스터디그룹_검색조건없이_여러건_조회_성공() {
        //given
        int totalDataNum = 20;
        IntStream.range(0, totalDataNum).forEach((i) -> {
            Member member = TestDataProvider.makeMember("박종훈" + i);
            memberRepository.save(member);

            CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제" + i);
            StudyGroup savedGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));
        });

        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(0, pageSize);
        StudyGroupSearchCond emptyCond = StudyGroupSearchCond.builder()
                .leaderName(null)
                .subject(null)
                .build();
        //when
        Page<StudyGroupDto> studyGroupList = studyGroupService.getStudyGroupList(pageRequest, emptyCond);

        //then
        assertThat(studyGroupList.getSize()).isEqualTo(pageSize);
        assertThat(studyGroupList.getTotalElements()).isEqualTo((long) totalDataNum);
    }

    @Test
    @Transactional
    void 스터디그룹_취소하는경우_Enrollment와Payment상태_변경_성공() {
        //given
        Member member1 = TestDataProvider.makeMember("Tester1");
        memberRepository.save(member1);
        Member member2 = TestDataProvider.makeMember("Tester2");
        memberRepository.save(member2);
        member2.chargePoint(50000L);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member1, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member1, request.toStudyGroupParam()));

        Enrollment enrollment = Enrollment.createEnrollment(member2, studyGroup);
        enrollmentRepository.save(enrollment);

        //when
        studyGroupService.cancelStudyGroup(studyGroup.getId());

        //then
        Enrollment findEnrollment = enrollmentRepository.findByIdWithAll(enrollment.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Payment payment = findEnrollment.getPayment();

        StudyGroup findStudyGroup = studyGroupRepository.findByIdWithEnrollments(studyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));


        assertThat(findEnrollment.getStatus()).isEqualTo(EnrollStatus.CANCEL);
        assertThat(findStudyGroup.getStatus()).isEqualTo(StudyGroupStatus.CANCEL);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);

    }

    @Test
    @Transactional
    void 스터디그룹_업데이트_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        UpdateStudyGroupRequest updateStudyGroupRequest = TestDataProvider.makeUpdateStudyGroupRequest("update_subject", studyGroup.getId());

        //when
        studyGroupService.updateStudyGroup(updateStudyGroupRequest.toUpdateStudyGroupParam());

        //then
        StudyGroup findStudyGroup = studyGroupRepository.findById(studyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findStudyGroup.getSubject()).isEqualTo(updateStudyGroupRequest.getSubject());
        assertThat(findStudyGroup.getPrice()).isEqualTo(updateStudyGroupRequest.getPrice());
        assertThat(findStudyGroup.getContents()).isEqualTo(updateStudyGroupRequest.getContents());
        assertThat(findStudyGroup.getContentsDetail()).isEqualTo(updateStudyGroupRequest.getContentsDetail());
        assertThat(findStudyGroup.getMaxSize()).isEqualTo(updateStudyGroupRequest.getMaxSize());
    }
}