package com.project.bookstudy.study_group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudyGroupControllerTest {

    @Autowired
    private StudyGroupService studyGroupService;
    @Autowired
    private StudyGroupRepository studyGroupRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /study-group 스터디 그룹 생성 성공")
    void createStudySuccess() throws Exception {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupRequest studyGroupRequest = getStudyGroupRequest(member, "test");

        String request = objectMapper.writeValueAsString(studyGroupRequest);

        //when
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

    @Test
    @DisplayName("GET /study-group/{id} 스터디 그룹 조회 성공")
    @Transactional
    void getStudySuccess() throws Exception {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        //when
        ResultActions resultActions = mockMvc
                .perform(get("/study-group/{id}", studyGroup.getId())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.subject").value(request.getSubject()))
                .andExpect(jsonPath("$.contents").value(request.getContents()))
                .andExpect(jsonPath("$.contentsDetail").value(request.getContentsDetail()))
                .andExpect(jsonPath("$.maxSize").value(request.getMaxSize()))
                .andExpect(jsonPath("$.price").value(request.getPrice()))
                .andExpect(jsonPath("$.leaderId").value(member.getId()))
                .andExpect(jsonPath("$.leaderName").value(member.getName()));
    }

    @Test
    @DisplayName("GET /study-group 스터디 그룹 여러건 조회 성공")
    @Transactional
    void getStudyListSuccess() throws Exception {
        //given
        int totalDataNum = 10;
        List<StudyGroup> studyGroups = new ArrayList<>();
        IntStream.range(0, totalDataNum).forEach((i) -> {
            Member member = memberRepository.save(createMember("박종훈" + i));
            CreateStudyGroupParam param = getStudyGroupRequest(member, "스터디 주제" + i).toStudyGroupParam();
            StudyGroup savedGroup = studyGroupRepository.save(StudyGroup.from(member, param));
            studyGroups.add(savedGroup);
        });


        //when
        ResultActions resultActions = mockMvc
                .perform(get("/study-group")
                        .param("page", "0")
                        .param("size", "5")
                        .param("leaderName", "박종훈3")
                        .param("subject", "주제")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /study-group 스터디 그룹 수정 성공")
    @Transactional
    void updateStudySuccess() throws Exception {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        UpdateStudyGroupRequest updateStudyGroupRequest = getUpdateStudyGroupRequest("update_subject", studyGroup.getId());
        String jsonRequest = objectMapper.writeValueAsString(updateStudyGroupRequest);

        //when
        ResultActions resultActions = mockMvc
                .perform(put("/study-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print());

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

    private UpdateStudyGroupRequest getUpdateStudyGroupRequest(String subject, Long id) {

        String contents = "update_contests";
        String contestsDetail = "update_detail";
        Long price = 500000L;
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
}
