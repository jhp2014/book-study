package com.project.bookstudy.study_group.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateEnrollmentResponse {

    private Long enrollmentId;

    @Builder
    public CreateEnrollmentResponse(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
}
