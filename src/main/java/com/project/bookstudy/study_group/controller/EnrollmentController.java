package com.project.bookstudy.study_group.controller;

import com.project.bookstudy.study_group.dto.EnrollmentDto;
import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.dto.request.EnrollmentSearchCond;
import com.project.bookstudy.study_group.dto.response.CreateEnrollmentResponse;
import com.project.bookstudy.study_group.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/enrollment/{id}")
    public void cancelEnrollment(@PathVariable("id") Long enrollmentId) {
        enrollmentService.cancel(enrollmentId);
    }

    @GetMapping("/enrollment/{id}")
    public EnrollmentDto getEnrollment(@PathVariable("id") Long enrollmentId) {
        //응답 정보 선별 하고싶으면, Dto에서 정보 선별 및 응답 객체 생성
        EnrollmentDto enrollmentDto = enrollmentService.getEnrollment(enrollmentId);
        return enrollmentDto;
    }

    @GetMapping("/enrollment")
    public Page<EnrollmentDto> getEnrollmentList(@PageableDefault Pageable pageable,
                                                 @ModelAttribute EnrollmentSearchCond cond) {
        //응답 정보 선별 하고싶으면, Dto에서 정보 선별 및 응답 객체 생성
        Page<EnrollmentDto> enrollmentList = enrollmentService.getEnrollmentList(pageable, cond.getMemberId());
        return enrollmentList;
    }
}
