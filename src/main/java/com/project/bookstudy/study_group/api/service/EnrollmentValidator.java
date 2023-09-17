package com.project.bookstudy.study_group.api.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentValidator {

    public void validate(Member member, StudyGroup studyGroup) {
        if (studyGroup.getLeader().getId() == member.getId()) {
            throw new IllegalStateException(ErrorMessage.STUDY_LEADER_APPLICATION_ERROR.getMessage());
        }

        if (!studyGroup.isApplicable()) {
            throw new IllegalStateException(ErrorMessage.STUDY_FULL.getMessage());
        }

        validateDuplicateApplication(member, studyGroup);
    }

    private void validateDuplicateApplication(Member member, StudyGroup studyGroup) {
        for (Enrollment enrollment : studyGroup.getEnrollments()) {
            if (enrollment.getMember() == member) {
                throw new IllegalStateException(ErrorMessage.DUPLICATE_APPLICATION_ERROR.getMessage());
            }
        }
    }
}
