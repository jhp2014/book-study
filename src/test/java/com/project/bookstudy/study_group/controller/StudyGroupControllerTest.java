package com.project.bookstudy.study_group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.dto.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.service.StudyGroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyGroupControllerTest {

    @Autowired private StudyGroupService studyGroupService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("StudyGroup 생성 성공")
    void createStudySuccess() throws Exception {
        //given
        Member member = memberRepository.save(createMember());
        CreateStudyGroupRequest studyGroupRequest = getStudyGroupRequest(member);

        String request = objectMapper.writeValueAsString(studyGroupRequest);

        //when
        System.out.println("request = " + request.toString());
        ResultActions resultActions = mockMvc.perform(post("/study-group")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print());

        //then
        resultActions
                .andExpect(jsonPath("$.studyGroupId").isNotEmpty())
                .andExpect(jsonPath("$.leaderId").value(member.getId()))
                .andExpect(status().isOk());

    }

    private Member createMember() {
        String name = "박종훈";
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

    private CreateStudyGroupRequest getStudyGroupRequest(Member member) {
        String subject = "test";
        String contents = "test_contests";
        String contestsDetail = "test_detail";
        Long price = 100000L;
        int maxSize = 10;

        LocalDateTime recruitStart = LocalDateTime.now();
        LocalDateTime recruitEnd = recruitStart.plusDays(3);
        LocalDateTime start = recruitEnd.plusDays(3);
        LocalDateTime end = start.plusDays(3);

        return CreateStudyGroupRequest.builder()
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
    }
}