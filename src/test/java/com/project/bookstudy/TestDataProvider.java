package com.project.bookstudy;

import com.project.bookstudy.board.dto.CreateCategoryRequest;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;

import java.time.LocalDateTime;

public class TestDataProvider {

    public static Member makeMember(String name) {
        String career = "career";
        String phone = "010-5453-5325";
        String email = "whdgnsdl35@gmail.com";

        return Member.builder()
                .name(name)
                .career(career)
                .phone(phone)
                .email(email)
                .build();
    }

    public static CreateStudyGroupRequest makeCreateStudyGroupRequest(Member member, String subject) {
        String contents = "test_contests";
        String contestsDetail = "test_detail";
        Long price = 1234L;
        int maxSize = 10;

        LocalDateTime recruitStart = LocalDateTime.now();
        LocalDateTime recruitEnd = recruitStart.plusDays(3);
        LocalDateTime start = recruitEnd.plusDays(3);
        LocalDateTime end = start.plusDays(3);

        CreateStudyGroupRequest request = CreateStudyGroupRequest.builder()
                .memberId(member.getId())
                .recruitmentStartAt(recruitStart)
                .recruitmentEndAt(recruitEnd)
                .studyStartAt(start)
                .studyEndAt(end)
                .subject(subject)
                .contents(contents)
                .contentsDetail(contestsDetail)
                .price(price)
                .maxSize(maxSize)
                .build();

        return request;
    }

    public static CreateEnrollmentRequest makeCreateEnrollmentRequest(StudyGroup studyGroup, Member member) {
        return CreateEnrollmentRequest.builder()
                .studyGroupId(studyGroup.getId())
                .memberId(member.getId())
                .build();
    }

    public static UpdateStudyGroupRequest makeUpdateStudyGroupRequest(String subject, Long id) {

        String contents = "update_contests";
        String contestsDetail = "update_detail";
        Long price = 100030L;
        int maxSize = 50;

        LocalDateTime recruitStart = LocalDateTime.now();
        LocalDateTime recruitEnd = recruitStart.plusDays(3);
        LocalDateTime start = recruitEnd.plusDays(3);
        LocalDateTime end = start.plusDays(3);

        UpdateStudyGroupRequest request = UpdateStudyGroupRequest.builder()
                .id(id)
                .maxSize(maxSize)
                .contents(contents)
                .subject(subject)
                .price(price)
                .contentsDetail(contestsDetail)
                .studyStartAt(start)
                .studyEndAt(end)
                .recruitmentStartAt(recruitStart)
                .recruitmentEndAt(recruitEnd)
                .build();

        return request;
    }

    public static CreateCategoryRequest getCreateCategoryRequest(Long parentId, StudyGroup studyGroup) {
        return CreateCategoryRequest.builder()
                .parentCategoryId(parentId)
                .subject("subject")
                .studyGroupId(studyGroup.getId())
                .build();
    }

}
