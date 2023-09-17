package com.project.bookstudy.study_group.api.service.response;

import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StudyGroupCreateResponse {
    private Long id;

    @Builder
    public StudyGroupCreateResponse(Long id) {
        this.id = id;
    }


    public static StudyGroupCreateResponse from(StudyGroup studyGroup) {
        return StudyGroupCreateResponse.builder()
                .id(studyGroup.getId())
                .build();
    }
}
