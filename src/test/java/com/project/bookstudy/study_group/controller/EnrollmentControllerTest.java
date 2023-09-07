package com.project.bookstudy.study_group.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.dto.request.CreateEnrollmentRequest;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.EnrollmentRepository;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class EnrollmentControllerTest {

    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyGroupRepository studyGroupRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /enrollment - enrollment 생성 성공")
    void createEnrollmentSuccess() throws Exception {
        //given
        Member member = createMember("박종훈");
        member.chargePoint(1000000L);
        memberRepository.save(member);

        CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제").getCreateStudyGroupParam();
        StudyGroup saveStudyGroup = studyGroupRepository.save(StudyGroup.from(member, param));

        CreateEnrollmentRequest request = CreateEnrollmentRequest.builder()
                .memberId(member.getId())
                .studyGroupId(saveStudyGroup.getId())
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);
        log.info(">>>>>>>>> {}", jsonRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/enrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk());


    }

    private Member createMember(String name) {
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

    private CreateStudyGroupRequest getStudyGroupRequest(Member member, String subject) {
        String contents = "test_contests";
        String contestsDetail = "test_detail";
        Long price = 100000L;
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

}