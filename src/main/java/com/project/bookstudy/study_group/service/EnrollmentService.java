package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long enroll(CreateEnrollmentRequest request) {
        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        if (!studyGroup.isApplicable()) {
            throw new IllegalStateException(ErrorMessage.FULL_STUDY.getMessage());
        }

        Enrollment enrollment = Enrollment.of(member, studyGroup);
        enrollmentRepository.save(enrollment);

        return enrollment.getId();
    }
}
