package com.project.bookstudy.study_group.service;

import com.project.bookstudy.TestDataProvider;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.EnrollStatus;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.PaymentStatus;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.dto.EnrollmentDto;
import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EnrollmentServiceTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyGroupRepository studyGroupRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    EnrollmentService enrollmentService;

    @Test
    @Transactional
    void Enrollment_생성_성공() {
        //given
        Member member1 = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member1);

        Member member2 = TestDataProvider.makeMember("박종훈2");
        memberRepository.save(member2);
        Long originMemberPoint = 1000000L;
        member2.chargePoint(originMemberPoint);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member1, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member1, request.toStudyGroupParam()));

        CreateEnrollmentRequest createEnrollmentRequest = TestDataProvider.makeCreateEnrollmentRequest(studyGroup, member2);

        //when
        Long enrollmentId = enrollmentService.enroll(createEnrollmentRequest);

        //then
        Enrollment findEnrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findEnrollment.getStatus()).isEqualTo(EnrollStatus.RESERVED);
        assertThat(findEnrollment.getMember().getId()).isEqualTo(member2.getId());
        assertThat(findEnrollment.getStudyGroup().getId()).isEqualTo(studyGroup.getId());
        assertThat(studyGroup.getEnrollments().contains(findEnrollment)).isTrue();
        assertThat(findEnrollment.getPayment().getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(member2.getPoint()).isEqualTo(originMemberPoint - studyGroup.getPrice());

    }


    @Test
    @Transactional
    void Enrollment_조회_성공() {
        //given
        Member member1 = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member1);

        Member member2 = TestDataProvider.makeMember("박종훈2");
        memberRepository.save(member2);
        Long originMemberPoint = 1000000L;
        member2.chargePoint(originMemberPoint);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member1, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member1, request.toStudyGroupParam()));

        CreateEnrollmentRequest createEnrollmentRequest = TestDataProvider.makeCreateEnrollmentRequest(studyGroup, member2);
        Enrollment enrollment = enrollmentRepository.save(Enrollment.createEnrollment(member2, studyGroup));

        //when
        EnrollmentDto enrollmentDto = enrollmentService.getEnrollment(enrollment.getId());

        //then
        assertThat(enrollmentDto.getId()).isEqualTo(enrollment.getId());
        assertThat(enrollmentDto.getStatus()).isEqualTo(enrollment.getStatus());
        assertThat(enrollmentDto.getPayment().getPrice()).isEqualTo(enrollment.getPayment().getPrice());
        assertThat(enrollmentDto.getPayment().getStatus()).isEqualTo(enrollment.getPayment().getStatus());
        assertThat(enrollmentDto.getStudyGroup().getId()).isEqualTo(enrollment.getStudyGroup().getId());
    }




}