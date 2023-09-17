package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.api.service.StudyGroupService;
import com.project.bookstudy.study_group.api.controller.request.StudyGroupUpdateRequest;
import com.project.bookstudy.study_group.api.service.response.StudyGroupCreateResponse;
import com.project.bookstudy.study_group.domain.*;
import com.project.bookstudy.study_group.api.controller.request.StudyGroupCreateRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Autowired
    private Clock clock;


    @DisplayName("스터디 그룹을 생성할 수 있다.")
    @Test
    @Transactional
    void createStudyGroupSuccess() {
        //given
        Member member = createMember("name", "email");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member.getId(),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");

        //when
        StudyGroupCreateResponse response = studyGroupService.createStudyGroup(request.toCreateServiceParam());


        //then
        StudyGroup studyGroup = studyGroupRepository.findById(response.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        assertThat(studyGroup.getId()).isNotNull();
        assertThat(studyGroup.getSubject()).isEqualTo(request.getSubject());
        assertThat(studyGroup.getContents()).isEqualTo(request.getContents());
        assertThat(studyGroup.getContentsDetail()).isEqualTo(request.getContentsDetail());
        assertThat(studyGroup.getStudyStartAt()).isEqualTo(request.getStudyStartAt());
        assertThat(studyGroup.getStudyEndAt()).isEqualTo(request.getStudyEndAt());
        assertThat(studyGroup.getMaxSize()).isEqualTo(request.getMaxSize());
        assertThat(studyGroup.getPrice()).isEqualTo(request.getPrice());
        assertThat(studyGroup.getRecruitmentStartAt()).isEqualTo(request.getRecruitmentStartAt());
        assertThat(studyGroup.getRecruitmentEndAt()).isEqualTo(request.getRecruitmentEndAt());

        assertThat(studyGroup.getLeader().getId()).isEqualTo(request.getMemberId());
    }



    @DisplayName("스터디 생성 시 스터디 그룹 시작 일이 스터디 그룹 종료일 보다 늦을 수 없다.")
    @Test
    @Transactional
    void createStudyGroupFail1() {
        //given
        LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 0, 0, 0);
        LocalDateTime endAt = startAt.minusSeconds(1);

        Member member = createMember("name", "email");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member.getId(), startAt, endAt,
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");

        //when //then
        assertThatThrownBy(() -> {
            studyGroupService.createStudyGroup(request.toCreateServiceParam());
        }, ErrorMessage.INVALID_DATE_RANGE_ERROR.getMessage(), IllegalStateException.class);
    }

    @DisplayName("스터디 생성 시 스터디 그룹 모집 시작 일이 스터디 그룹 모집 종료일 보다 늦을 수 없다.")
    @Test
    @Transactional
    void createStudyGroupFail2() {
        //given
        LocalDateTime recruitmentStart = LocalDateTime.of(2023, 9, 1, 0, 0, 0);
        LocalDateTime recruitmentEnd = recruitmentStart.minusSeconds(1);
        LocalDateTime startAt = recruitmentEnd.plusDays(10);
        LocalDateTime endAt = startAt.plusDays(1);
        Member member = createMember("name", "email");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member.getId(), startAt, endAt, recruitmentStart, recruitmentEnd, "subject", "contents");

        //when //then
        assertThatThrownBy(() -> {
            studyGroupService.createStudyGroup(request.toCreateServiceParam());
        }, ErrorMessage.INVALID_DATE_RANGE_ERROR.getMessage(), IllegalStateException.class);
    }

    @DisplayName("스터디 생성 시 스터디 그룹 모집 시작 일이 스터디 그룹 시작 일 보다 늦을 수 없다.")
    @Test
    @Transactional
    void createStudyGroupFail3() {
        //given
        LocalDateTime recruitmentStart = LocalDateTime.of(2023, 9, 1, 0, 0, 0);
        LocalDateTime recruitmentEnd = recruitmentStart.plusDays(2);
        LocalDateTime startAt = recruitmentStart.minusSeconds(1);
        LocalDateTime endAt = startAt.plusDays(1);
        Member member = createMember("name", "email");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member.getId(), startAt, endAt, recruitmentStart, recruitmentEnd, "subject", "contents");

        //when //then
        assertThatThrownBy(() -> {
            studyGroupService.createStudyGroup(request.toCreateServiceParam());
        }, ErrorMessage.INVALID_DATE_RANGE_ERROR.getMessage(), IllegalStateException.class);
    }

    @DisplayName("스터디 그룹을 생성시 멤버가 없으면 예외가 발생 한다.")
    @Test
    @Transactional
    void createStudyGroupFailCauseMemberId() {
        //given
        Member member = createMember("name", "email");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member.getId() + 1,
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");

        //when //then
        assertThrows(IllegalArgumentException.class, () -> {
            studyGroupService.createStudyGroup(request.toCreateServiceParam());
        });
    }

    @DisplayName("스터디 그룹 단건 조회 시 스터디 그룹이 없는 경우 예외가 발생 한다.")
    @Test
    @Transactional
    void getStudyGroupFailCauseId() {
        //given
        Member member = createMember("name", "email");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member.getId() + 1,
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");

        StudyGroup studyGroup = request.toCreateServiceParam().toEntityWithLeader(member);
        studyGroupRepository.save(studyGroup);

        //when //then
        assertThrows(IllegalArgumentException.class, () -> {
            studyGroupService.getStudyGroup(studyGroup.getId() + 1);
        });
    }


    private StudyGroupCreateRequest createStudyCreateGroupRequest(Long memberId, LocalDateTime studyStartAt, LocalDateTime studyEndAt, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt, String subject, String contents) {
        return StudyGroupCreateRequest.builder()
                .memberId(memberId)
                .subject(subject)
                .contents(contents)
                .contentsDetail("contentsDetail")
                .maxSize(10)
                .price(25000L)
                .studyStartAt(studyStartAt)
                .studyEndAt(studyEndAt)
                .recruitmentStartAt(recruitmentStartAt)
                .recruitmentEndAt(recruitmentEndAt)
                .build();
    }

    private Member createMember(String name, String email) {
        return Member.builder()
                .name(name)
                .career("career")
                .phone("phone")
                .email(email)
                .build();
    }


    @DisplayName("스터디 그룹 정보를 수정 할 수 있다.")
    @Test
    @Transactional
    void updateStudyGroupSuccess() {
        //given
        Member member = createMember("스터디 리더", "email");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member.getId(),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");
        StudyGroup studyGroup = request.toCreateServiceParam().toEntityWithLeader(member);
        studyGroupRepository.save(studyGroup);

        StudyGroupUpdateRequest updateRequest = createStudyUpdateRequest(studyGroup);

        //when

        studyGroupService.updateStudyGroup(updateRequest.toUpdateServiceParam());


        //then
        StudyGroup findStudyGroup = studyGroupRepository.findById(studyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findStudyGroup.getSubject()).isEqualTo(updateRequest.getSubject());
        assertThat(findStudyGroup.getContents()).isEqualTo(updateRequest.getContents());
        assertThat(findStudyGroup.getContentsDetail()).isEqualTo(updateRequest.getContentsDetail());
        assertThat(findStudyGroup.getMaxSize()).isEqualTo(updateRequest.getMaxSize());
        assertThat(findStudyGroup.getPrice()).isEqualTo(updateRequest.getPrice());
        assertThat(findStudyGroup.getRecruitmentStartAt()).isEqualTo(updateRequest.getRecruitmentStartAt());
        assertThat(findStudyGroup.getRecruitmentEndAt()).isEqualTo(updateRequest.getRecruitmentEndAt());
        assertThat(findStudyGroup.getStudyStartAt()).isEqualTo(updateRequest.getStudyStartAt());
        assertThat(findStudyGroup.getStudyEndAt()).isEqualTo(updateRequest.getStudyEndAt());

    }

    private StudyGroupUpdateRequest createStudyUpdateRequest(StudyGroup studyGroup) {
        return StudyGroupUpdateRequest.builder()
                .studyGroupId(studyGroup.getId())
                .subject("changed subject")
                .contents("changed contents")
                .contentsDetail("changed contentsDetail")
                .maxSize(300)
                .price(2500L)
                .recruitmentStartAt(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .recruitmentEndAt(LocalDateTime.of(2024, 1, 1, 1, 1, 2))
                .studyStartAt(LocalDateTime.of(2024, 1, 1, 1, 1, 3))
                .studyEndAt(LocalDateTime.of(2024, 1, 1, 1, 1, 4))
                .build();
    }

    @DisplayName("스터디 그룹을 취소하는 경우 이미 신청한 사람들에게 환불을 해주어야 한다.")
    @Test
    @Transactional
    void cancelSuccess() {
        //given
        Long initPoint = 500000L;

        Member member1 = createMember("스터디 리더", "email");
        memberRepository.save(member1);
        Member member2 = createMember("스터디 신청 회원", "email");
        member2.chargePoint(initPoint);
        memberRepository.save(member2);

        StudyGroupCreateRequest request = createStudyCreateGroupRequest(member1.getId(),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");
        StudyGroup studyGroup = request.toCreateServiceParam().toEntityWithLeader(member1);
        studyGroupRepository.save(studyGroup);

        Enrollment enrollment = Enrollment.of(member2, studyGroup, LocalDateTime.now(clock));
        enrollmentRepository.save(enrollment);

        //when
        studyGroupService.cancelStudyGroup(studyGroup.getId());

        //then
        Enrollment findEnrollment = enrollmentRepository.findByIdWithAll(enrollment.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Payment payment = findEnrollment.getPayment();

        StudyGroup findStudyGroup = studyGroupRepository.findByIdFetchEnrollment(studyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));


        assertThat(findEnrollment.getStatus()).isEqualTo(EnrollStatus.CANCEL);
        assertThat(findStudyGroup.getStatus()).isEqualTo(StudyGroupStatus.CANCEL);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }
}