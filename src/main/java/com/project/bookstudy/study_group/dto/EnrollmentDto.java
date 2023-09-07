package com.project.bookstudy.study_group.dto;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.EnrollStatus;
import com.project.bookstudy.study_group.domain.Enrollment;
import com.project.bookstudy.study_group.domain.Payment;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
public class EnrollmentDto {

    private Long id;
    private EnrollStatus status;
    private StudyGroupDto studyGroup;
    private PaymentDto payment;

    @Builder
    private EnrollmentDto(Long id, EnrollStatus status, StudyGroupDto studyGroup, PaymentDto payment) {
        this.id = id;
        this.status = status;
        this.studyGroup = studyGroup;
        this.payment = payment;
    }

    public static EnrollmentDto fromEntity(Enrollment enrollment) {
        StudyGroup studyGroup = enrollment.getStudyGroup();
        Payment payment = enrollment.getPayment();

        return EnrollmentDto.builder()
                .id(enrollment.getId())
                .status(enrollment.getStatus())
                .studyGroup(StudyGroupDto.fromEntity(studyGroup, studyGroup.getLeader()))
                .payment(PaymentDto.fromEntity(payment))
                .build();
    }
}
