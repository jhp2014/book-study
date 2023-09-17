package com.project.bookstudy.study_group.service;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.api.service.EnrollmentService;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.EnrollmentCreateRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.PaymentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class EnrollConcurrencyTest {

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


    /**
     * Feature: 스터디 신청 동시성 테스트
     * Background
     *    스터디 그룹 MaxSize = 2
     *    현재 한명이 신청해서 한자리만 남은 상태
     * <p>
     * Scenario: 이 상태에서 5명의 회원이 동시에 신청
     * <p>
     * Then IllegalStatusException(스터디 자리 부족) 예외 발생
     */
    @DisplayName("스터디 그룹 등록시 동시성 이슈가 발생 하지 않는다.")
    @Test
    void enrollConcurrencyIssueCheck() throws InterruptedException {


        //given
        Long memberPoint = 50_000L;
        Long studyPrice = 10_000L;
        int maxSeat = 2;

        Member leader = createMember();
        Member enrolledMember = createMember();
        Member member1 = createMember();
        Member member2 = createMember();
        Member member3 = createMember();
        Member member4 = createMember();
        Member member5 = createMember();

        enrolledMember.chargePoint(memberPoint);
        member1.chargePoint(memberPoint);
        member2.chargePoint(memberPoint);
        member3.chargePoint(memberPoint);
        member4.chargePoint(memberPoint);
        member5.chargePoint(memberPoint);

        memberRepository.saveAll(List.of(leader, enrolledMember, member1, member2, member3,member4, member5));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(maxSeat)
                .price(studyPrice)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

        Enrollment enrollment1 = Enrollment.of(enrolledMember, studyGroup, LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        enrollmentRepository.save(enrollment1);


        //when
        /**
         * 남은 1자리를 놓고 5명의 회원이 동시 신청 준비
         */
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Callable<Boolean> apply1 = createCallableForEnroll(member1.getId(), studyGroup.getId());
        Callable<Boolean> apply2 = createCallableForEnroll(member2.getId(), studyGroup.getId());
        Callable<Boolean> apply3 = createCallableForEnroll(member3.getId(), studyGroup.getId());
        Callable<Boolean> apply4 = createCallableForEnroll(member4.getId(), studyGroup.getId());
        Callable<Boolean> apply5 = createCallableForEnroll(member5.getId(), studyGroup.getId());

        List<Future<Boolean>> futures = executorService.invokeAll(List.of(apply1, apply2, apply3, apply4, apply5));

        // then
        StudyGroup findStudyGroup = studyGroupRepository.findByIdFetchEnrollment(studyGroup.getId()).orElseThrow();

        assertThat(findStudyGroup.getEnrollments()).hasSize(maxSeat);
        assertThat(futures)
                .filteredOn(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);}}, true)
                .hasSize(1);

    }

    @DisplayName("기존 스터디 그룹 신청 메서드는 동시성 이슈가 발생한다.")
    @Test
    void enrollFailCausedByConcurrencyIssue() throws InterruptedException {

        //given
        Long memberPoint = 50_000L;
        Long studyPrice = 10_000L;
        int maxSeat = 2;

        Member leader = createMember();
        Member enrolledMember = createMember();
        Member member1 = createMember();
        Member member2 = createMember();
        Member member3 = createMember();
        Member member4 = createMember();
        Member member5 = createMember();

        enrolledMember.chargePoint(memberPoint);
        member1.chargePoint(memberPoint);
        member2.chargePoint(memberPoint);
        member3.chargePoint(memberPoint);
        member4.chargePoint(memberPoint);
        member5.chargePoint(memberPoint);

        memberRepository.saveAll(List.of(leader, enrolledMember, member1, member2, member3,member4, member5));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(maxSeat)
                .price(studyPrice)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

        Enrollment enrollment1 = Enrollment.of(enrolledMember, studyGroup, LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        enrollmentRepository.save(enrollment1);


        //when
        /**
         * 남은 1자리를 놓고 5명의 회원이 동시 신청 준비
         */
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Callable<Boolean> apply1 = createCallableForEnrollFail(member1.getId(), studyGroup.getId());
        Callable<Boolean> apply2 = createCallableForEnrollFail(member2.getId(), studyGroup.getId());
        Callable<Boolean> apply3 = createCallableForEnrollFail(member3.getId(), studyGroup.getId());
        Callable<Boolean> apply4 = createCallableForEnrollFail(member4.getId(), studyGroup.getId());
        Callable<Boolean> apply5 = createCallableForEnrollFail(member5.getId(), studyGroup.getId());

        List<Future<Boolean>> futures = executorService.invokeAll(List.of(apply1, apply2, apply3, apply4, apply5));

        StudyGroup findStudyGroup = studyGroupRepository.findByIdFetchEnrollment(studyGroup.getId())
                .orElseThrow();
        // then
        assertThat(findStudyGroup.getEnrollments().size()).isGreaterThan(maxSeat);
    }

    private Callable<Boolean> createCallableForEnroll(Long memberId, Long studyId) {

        Callable<Boolean> apply = () -> {
            Boolean result = null;
            try {
                enrollmentService.enroll(memberId, studyId);
                log.info(">>>>> member {} 성공: {}" ,memberId, Thread.currentThread().getName());
                result = true;
            } catch (IllegalStateException e) {
                log.info(">>>>> member {} 실패: {}" ,memberId, Thread.currentThread().getName());
                result = false;
            }
            return result;
        };
        return apply;
    }

    private Member createMember() {
        return Member.builder()
                .name("member")
                .email("email2")
                .build();
    }

    private Callable<Boolean> createCallableForEnrollFail(Long memberId, Long studyId) {

        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .studyGroupId(studyId)
                .memberId(memberId)
                .build();

        Callable<Boolean> apply = () -> {
            Boolean result = null;
            try {
                enrollmentService.enroll(request);
                log.info(">>>>> member {} 성공: {}" ,memberId, Thread.currentThread().getName());
                result = true;
            } catch (IllegalStateException e) {
                log.info(">>>>> member {} 실패: {}" ,memberId, Thread.currentThread().getName());
                result = false;
            }
            return result;
        };
        return apply;
    }
}
