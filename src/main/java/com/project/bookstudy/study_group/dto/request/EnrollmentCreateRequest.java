package com.project.bookstudy.study_group.dto.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class EnrollmentCreateRequest {

    @NotNull(message = "회원 아이디는 필수입니다.")
    private Long memberId;
    @NotNull(message = "스터디 그룹 아이디는 필수입니다.")
    private Long studyGroupId;

    @Builder
    private EnrollmentCreateRequest(Long memberId, Long studyGroupId) {
        this.memberId = memberId;
        this.studyGroupId = studyGroupId;
    }
}
