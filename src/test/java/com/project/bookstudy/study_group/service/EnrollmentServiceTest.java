package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
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
    @DisplayName("Enrollment 생성 성공")
    @Transactional
    void createEnrollmentSuccess() {
        //given
        Member member = createMember("박종훈");
        member.chargePoint(1000000L);

        Member savedMember = memberRepository.save(member);

        CreateStudyGroupParam param = getStudyGroupRequest(savedMember, "스터디 주제").getCreateStudyGroupParam();
        StudyGroup saveStudyGroup = studyGroupRepository.save(StudyGroup.from(savedMember, param));

        CreateEnrollmentRequest request = CreateEnrollmentRequest.builder()
                .memberId(savedMember.getId())
                .studyGroupId(saveStudyGroup.getId())
                .build();

        //when
        Long enrollmentId = enrollmentService.enroll(request);

        entityManager.flush();
        entityManager.clear();

        //then
        Enrollment findEnrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.NO_ENTITY.getMessage()));
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


}