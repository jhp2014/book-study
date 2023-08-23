package com.project.bookstudy.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.dto.CreateMemberRequest;
import com.project.bookstudy.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 성공")
    @Transactional
    void successJoin() throws Exception {
        //given
        String name = "name";
        String password = "!@tt6018lw";
        String phone = "010-7474-8181";
        String email = "whdgnsdl35@naver.com";
        String career = "취준생";

        CreateMemberRequest request = CreateMemberRequest.builder()
                .name(name)
                .password(password)
                .phone(phone)
                .email(email)
                .career(career)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/member")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andDo(MockMvcResultHandlers.print());

        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(request.getName()));
    }


    @Test
    @DisplayName("회원가입요청 실패")
    void failJoinByBadRequest() throws Exception {

        //given
        String name = "";
        String password = "password";
        String phone = "";
        String email = "whdgnsdlnaver.com";
        String career = "취준생";

        CreateMemberRequest request = CreateMemberRequest.builder()
                .name(name)
                .password(password)
                .phone(phone)
                .email(email)
                .career(career)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        //when
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/member")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andDo(MockMvcResultHandlers.print());

        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorInfoList[0].field").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorInfoList[0].errorMessage").isNotEmpty());
    }

    @Test
    @DisplayName("이메일 중복으로 인한 회원가입 실패")
    @Transactional
    void failJoinByDuplicateEmail() throws Exception {

        //given
        String name = "name";
        String password = "!@tt6018lw";
        String phone = "010-7474-8181";
        String email = "whdgnsdl35@naver.com";
        String career = "취준생";

        CreateMemberRequest request = CreateMemberRequest.builder()
                .name(name)
                .password(password)
                .phone(phone)
                .email(email)
                .career(career)
                .build();

        Member member = Member.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .career(career)
                .phone(phone)
                .build();
        memberRepository.save(member);

        String requestJson = objectMapper.writeValueAsString(request);

        //when
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/member")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andDo(MockMvcResultHandlers.print());

        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorInfoList[0].field").value("email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorInfoList[0].errorMessage").value("같은 이메일이 존재합니다."));
    }
}