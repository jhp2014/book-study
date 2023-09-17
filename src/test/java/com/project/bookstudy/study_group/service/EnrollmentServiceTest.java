package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.api.service.EnrollmentService;
import com.project.bookstudy.study_group.domain.EnrollStatus;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.EnrollmentCreateRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.PaymentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
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
    @Autowired
    PaymentRepository paymentRepository;

    @AfterEach
    void tearDown() {
        enrollmentRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        studyGroupRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원은 스터디 그룹에 참여할 수 있다.")
    @Test
    void enrollSuccess() {

        //given
        Long memberPoint = 50_000L;
        Long studyPrice = 10_000L;
        int availableSeat = 1;

        Member leader = createMember();
        Member member = createMember();
        member.chargePoint(memberPoint);
        memberRepository.saveAll(List.of(leader, member));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(availableSeat)
                .price(studyPrice)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

        //when
        Long enrollmentId = enrollmentService.enroll(member.getId(), studyGroup.getId());

        //then
        Enrollment findEnrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        Member findMember = memberRepository.findById(member.getId()).orElseThrow();
        StudyGroup findStudyGroup = studyGroupRepository.findByIdFetchEnrollment(studyGroup.getId()).orElseThrow();

        SoftAssertions.assertSoftly( softly -> {
            softly.assertThat(findEnrollment.getMember()).isEqualTo(findMember);
            softly.assertThat(findEnrollment.getStudyGroup().getId()).isEqualTo(studyGroup.getId());
            softly.assertThat(findEnrollment.getStatus()).isEqualTo(EnrollStatus.RESERVED);
            softly.assertThat(findMember.getPoint()).isEqualTo(memberPoint - studyPrice);
            softly.assertThat(findStudyGroup.isApplicable()).isFalse();
        });
    }

    @DisplayName("리더는 자신이 생성한 스터디 그룹에 신청을 할 수 없다.")
    @Test
    void enrollFailCausedByLeader() {
        Member leader = createMember();
        Member member = createMember();
        member.chargePoint(50_000L);
        memberRepository.saveAll(List.of(leader, member));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(3)
                .price(10_000L)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

        Enrollment enrollment = Enrollment.of(member, studyGroup, LocalDateTime.of(2023,1,1,1,1,1));
        enrollmentRepository.save(enrollment);

        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .memberId(leader.getId())
                .studyGroupId(studyGroup.getId())
                .build();

        //when //then
        assertThatThrownBy(() -> enrollmentService.enroll(leader.getId(), studyGroup.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.STUDY_LEADER_APPLICATION_ERROR.getMessage());
    }

    @DisplayName("회원은 동일한 스터디 그룹에 중복 신청을 할 수 없다.")
    @Test
    void enrollFailCausedByDuplication() {
        Member leader = createMember();
        Member member = createMember();
        member.chargePoint(50_000L);
        memberRepository.saveAll(List.of(leader, member));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(3)
                .price(10_000L)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

        Enrollment enrollment = Enrollment.of(member, studyGroup, LocalDateTime.of(2023,1,1,1,1,1));
        enrollmentRepository.save(enrollment);

        //when //then
        assertThatThrownBy(() -> enrollmentService.enroll(member.getId(), studyGroup.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.DUPLICATE_APPLICATION_ERROR.getMessage());
    }

    @DisplayName("스터디 그룹 신청 시 포인트가 부족하면 신청할 수 없다.")
    @Test
    void enrollFailCausedByNotEnoughPoint() {

        //given
        Long memberPoint = 5_00L;
        Long studyPrice = 10_000L;

        Member leader = createMember();
        Member member = createMember();
        member.chargePoint(memberPoint);
        memberRepository.saveAll(List.of(leader, member));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(3)
                .price(studyPrice)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

        //when //then
        assertThatThrownBy(() -> enrollmentService.enroll(member.getId(), studyGroup.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.NOT_ENOUGH_POINT.getMessage());
    }

    @DisplayName("스터디 그룹 신청 시 자리가 가득 차면 신청할 수 없다.")
    @Test
    void enrollFailCausedByNoAvailableSeat() {

        //given
        Long memberPoint = 50_000L;
        Long studyPrice = 10_000L;

        Member leader = createMember();
        Member member1 = createMember();
        Member member2 = createMember();
        Member member3 = createMember();
        member1.chargePoint(memberPoint);
        member2.chargePoint(memberPoint);
        member3.chargePoint(memberPoint);

        memberRepository.saveAll(List.of(leader, member1, member2, member3));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(2)
                .price(studyPrice)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

        Enrollment enrollment1 = Enrollment.of(member1, studyGroup, LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        Enrollment enrollment2 = Enrollment.of(member2, studyGroup, LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        enrollmentRepository.saveAll(List.of(enrollment1, enrollment2));

        //when //then
        assertThatThrownBy(() -> enrollmentService.enroll(member3.getId(), studyGroup.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.STUDY_FULL.getMessage());
    }

    private Member createMember() {
        return Member.builder()
                .name("member")
                .email("email2")
                .build();
    }








/*
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

        StudyGroupCreateRequest request = TestDataProvider.makeCreateStudyGroupRequest(member1, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member1, request.toCreateServiceParam()));

        EnrollmentCreateRequest enrollmentCreateRequest = TestDataProvider.makeCreateEnrollmentRequest(studyGroup, member2);
        Enrollment enrollment = enrollmentRepository.save(Enrollment.of(member2, studyGroup, LocalDateTime.now(clock)));

        //when
        EnrollmentDto enrollmentDto = enrollmentService.getEnrollment(enrollment.getId());

        //then
        assertThat(enrollmentDto.getId()).isEqualTo(enrollment.getId());
        assertThat(enrollmentDto.getStatus()).isEqualTo(enrollment.getStatus());
        assertThat(enrollmentDto.getPayment().getPrice()).isEqualTo(enrollment.getPayment().getPrice());
        assertThat(enrollmentDto.getPayment().getStatus()).isEqualTo(enrollment.getPayment().getStatus());
        assertThat(enrollmentDto.getStudyGroup().getId()).isEqualTo(enrollment.getStudyGroup().getId());
    }*/
}