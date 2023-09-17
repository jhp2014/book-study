package com.project.bookstudy.study_group.api.controller;

import com.project.bookstudy.common.api.ApiResonse;
import com.project.bookstudy.study_group.api.controller.request.StudyGroupCreateRequest;
import com.project.bookstudy.study_group.api.controller.request.StudyGroupUpdateRequest;
import com.project.bookstudy.study_group.api.service.response.StudyGroupCreateResponse;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.project.bookstudy.study_group.api.service.response.StudyGroupGetResponse;
import com.project.bookstudy.study_group.api.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping("/api/v1/study-group")
    public ApiResonse<StudyGroupCreateResponse> createStudyGroup(@Valid @RequestBody StudyGroupCreateRequest request) {
        StudyGroupCreateResponse response = studyGroupService.createStudyGroup(request.toCreateServiceParam());
        return ApiResonse.ok(response);
    }

    @GetMapping("/api/v1/study-group/{id}")
    public ApiResonse<StudyGroupGetResponse> getStudyGroup(@PathVariable("id") Long studyId) {
        StudyGroupGetResponse response = studyGroupService.getStudyGroup(studyId);
        return ApiResonse.ok(response);
    }

    @GetMapping("/api/v1/study-group")
    public ApiResonse<Page<StudyGroupGetResponse>> getStudyGroupList(@PageableDefault Pageable pageable,
                                                         @ModelAttribute StudyGroupSearchCond cond) {
        //검색 시, SQL Injection 방어는?
        Page<StudyGroupGetResponse> studyGroupList = studyGroupService.getStudyGroupList(pageable, cond);
        return ApiResonse.ok(studyGroupList);
    }

    @PutMapping("/api/v1/study-group")
    public ApiResonse<Object> updateStudyGroup(@Valid @RequestBody StudyGroupUpdateRequest request) {
        studyGroupService.updateStudyGroup(request.toUpdateServiceParam());
        return ApiResonse.ok(null);
    }

    @DeleteMapping("/study-group/{id}")
    public void deleteStudyGroup(@PathVariable("id") Long studyId) {
        studyGroupService.cancelStudyGroup(studyId);
    }
}
