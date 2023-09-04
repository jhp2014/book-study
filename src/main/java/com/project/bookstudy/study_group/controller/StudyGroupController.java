package com.project.bookstudy.study_group.controller;

import com.project.bookstudy.study_group.dto.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.CreateStudyGroupResponse;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping("/study-group")
    public CreateStudyGroupResponse createStudyGroup(@RequestBody CreateStudyGroupRequest request) {
        StudyGroupDto studyGroupDto = studyGroupService
                .createStudyGroup(request.createStudyGroupParam(), request.getMemberId());

        return CreateStudyGroupResponse.builder()
                .studyGroupId(studyGroupDto.getId())
                .leaderId(studyGroupDto.getLeader().getId())
                .build();
    }
}
