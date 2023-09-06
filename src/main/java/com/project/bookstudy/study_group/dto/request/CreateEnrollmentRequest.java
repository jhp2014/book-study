package com.project.bookstudy.study_group.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateEnrollmentRequest {

    private Long memberId;
    private Long studyGroupId;

    @Builder
    private CreateEnrollmentRequest(Long memberId, Long studyGroupId) {
        this.memberId = memberId;
        this.studyGroupId = studyGroupId;
    }
}
