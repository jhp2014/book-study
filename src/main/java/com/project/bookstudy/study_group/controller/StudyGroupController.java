package com.project.bookstudy.study_group.controller;

import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.response.CreateStudyGroupResponse;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping("/study-group")
    public CreateStudyGroupResponse createStudyGroup(@RequestBody CreateStudyGroupRequest request) {
        StudyGroupDto studyGroupDto = studyGroupService
                .createStudyGroup(request.getMemberId(), request.getCreateStudyGroupParam());

        return CreateStudyGroupResponse.builder()
                .studyGroupId(studyGroupDto.getId())
                .leaderId(studyGroupDto.getLeaderId())
                .build();
    }

    @GetMapping("/study-group")
    public Page<StudyGroupDto> getStudyGroupList(Pageable pageable) {
        //검색 기능 추가 해야한다.
        return studyGroupService.getStudyGroupList(pageable);
    }

    @GetMapping("/study-group/{id}")
    public StudyGroupDto getStudyGroup(@PathVariable("id") Long studyId) {
        return studyGroupService.getStudyGroup(studyId);
    }

    @PostMapping("/study-group/{id}")
    public void updateStudyGroup(@PathVariable("id") Long studyId
            , @RequestBody UpdateStudyGroupRequest request) {

        studyGroupService.updateStudyGroup(studyId, request.getUpdateStudyGroupParam());

    }
}
