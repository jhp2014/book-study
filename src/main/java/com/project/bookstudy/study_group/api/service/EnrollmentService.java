package com.project.bookstudy.study_group.api.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.common.aop.redisson.DistributedLock;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.EnrollmentDto;
import com.project.bookstudy.study_group.dto.request.EnrollmentCreateRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;
    private final EnrollmentValidator enrollmentValidator;
    private final Clock clock;

    /*
    * IllegalStateException
    *
    *
    * */
    @Transactional
    public Long enroll(EnrollmentCreateRequest request){

        StudyGroup studyGroup = studyGroupRepository.findByIdFetchEnrollment(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        //redis 분산 락 적용 필요 하다.

        enrollmentValidator.validate(member, studyGroup);

        Enrollment enrollment = Enrollment.of(member, studyGroup, LocalDateTime.now(clock));
        enrollmentRepository.save(enrollment);

        return enrollment.getId();
    }

    @DistributedLock(key = "#studyId")
    @Transactional
    public Long enroll(Long memberId, Long studyId){

        StudyGroup studyGroup = studyGroupRepository.findByIdFetchEnrollment(studyId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        enrollmentValidator.validate(member, studyGroup);

        Enrollment enrollment = Enrollment.of(member, studyGroup, LocalDateTime.now(clock));
        enrollmentRepository.save(enrollment);

        return enrollment.getId();
    }

    @Transactional
    public void cancel(Long enrollmentId) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        if (enrollment.getStudyGroup().isStarted()) {
            throw new IllegalStateException(ErrorMessage.STUDY_CANCEL_FAIL.getMessage());
        }
        enrollment.cancel();
    }

    public EnrollmentDto getEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByIdWithAll(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        //스터디 정보(StudyDto), 신청일(Payment 에 있음), PaymentDto 이렇게 내려 준다.
        EnrollmentDto enrollmentDto = EnrollmentDto.fromEntity(enrollment);
        return enrollmentDto;
    }

    public Page<EnrollmentDto> getEnrollmentList(Pageable pageable, Long memberId) {
        Page<Enrollment> enrollments = enrollmentRepository.searchEnrollment(pageable, memberId);
        Page<EnrollmentDto> enrollmentDtoList = enrollments
                .map(content -> EnrollmentDto.fromEntity(content));
        return enrollmentDtoList;
    }
}
