package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.EnrollmentDto;
import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long enroll(CreateEnrollmentRequest request){

        //Collection Fetch Join → Batch Size 적용 고려
        StudyGroup studyGroup = studyGroupRepository.findByIdWithEnrollments(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        if (!studyGroup.isApplicable()) {
            throw new IllegalStateException(ErrorMessage.FULL_STUDY.getMessage());
        }

        Enrollment enrollment = Enrollment.createEnrollment(member, studyGroup);
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
