package com.project.bookstudy.study_group.controller;

import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.dto.response.CreateEnrollmentResponse;
import com.project.bookstudy.study_group.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enrollment")
    public CreateEnrollmentResponse createEnrollment(@RequestBody CreateEnrollmentRequest request) {
        Long enrollmentId = enrollmentService.enroll(request);

        CreateEnrollmentResponse response = CreateEnrollmentResponse.builder()
                .enrollmentId(enrollmentId)
                .build();
        return response;
    }

}
