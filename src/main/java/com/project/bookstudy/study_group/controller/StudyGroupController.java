package com.project.bookstudy.study_group.controller;

import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.response.CreateStudyGroupResponse;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping("/study-group")
    public CreateStudyGroupResponse createStudyGroup(@RequestBody CreateStudyGroupRequest request) {
        StudyGroupDto studyGroupDto = studyGroupService
                .createStudyGroup(request.getMemberId(), request.toStudyGroupParam());

        return CreateStudyGroupResponse.builder()
                .studyGroupId(studyGroupDto.getId())
                .leaderId(studyGroupDto.getLeaderId())
                .build();
    }

    @GetMapping("/study-group")
    public Page<StudyGroupDto> getStudyGroupList(@PageableDefault Pageable pageable,
                                                 @ModelAttribute StudyGroupSearchCond cond) {
        //검색 기능 추가 해야한다.
        return studyGroupService.getStudyGroupList(pageable, cond);
    }

    @GetMapping("/study-group/{id}")
    public StudyGroupDto getStudyGroup(@PathVariable("id") Long studyId) {
        return studyGroupService.getStudyGroup(studyId);
    }

    @PutMapping("/study-group")
    public void updateStudyGroup(@RequestBody UpdateStudyGroupRequest request) {
        studyGroupService.updateStudyGroup(request.getUpdateStudyGroupParam());
    }

    @DeleteMapping("/study-group/{id}")
    public void deleteStudyGroup(@PathVariable("id") Long studyId) {
        studyGroupService.cancelStudyGroup(studyId);
    }
}
