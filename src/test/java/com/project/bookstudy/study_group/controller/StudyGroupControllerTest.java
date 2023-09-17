package com.project.bookstudy.study_group.controller;

import com.project.bookstudy.ControllerTestSupport;
import com.project.bookstudy.member.domain.MemberStatus;
import com.project.bookstudy.member.domain.Role;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.study_group.api.controller.request.StudyGroupCreateRequest;
import com.project.bookstudy.study_group.api.service.response.StudyGroupCreateResponse;
import com.project.bookstudy.study_group.api.service.response.StudyGroupGetResponse;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class StudyGroupControllerTest extends ControllerTestSupport {

    @DisplayName("스터디 그룹을 생성 한다.")
    @Test
    void createStudyGroupSuccess() throws Exception {
        //given
        StudyGroupCreateRequest request = makeStudyGroupCreateRequest("subject_test");


        given(studyGroupService.createStudyGroup(any()))
                .willReturn(StudyGroupCreateResponse.builder()
                        .id(1L)
                        .build());

        //when //then
        mockMvc.perform(post("/api/v1/study-group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print())
                ;
    }

    @DisplayName("스터디 그룹을 생성시 요청 값을 검증 한다.")
    @Test
    void createStudyGroupFail() throws Exception {
        //given
        StudyGroupCreateRequest request = makeStudyGroupCreateRequest("");


        given(studyGroupService.createStudyGroup(any()))
                .willReturn(StudyGroupCreateResponse.builder()
                        .id(1L)
                        .build());

        //when //then
        mockMvc.perform(post("/api/v1/study-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print())
        ;
    }

    @DisplayName("스터디 ID를 통해 스터디 그룹을 조회할 수 있다.")
    @Test
    void getStudyGroupSuccess() throws Exception {
        //given
        StudyGroupCreateRequest request = makeStudyGroupCreateRequest("");

        MemberDto memberDto = createMemberDto(1L);
        StudyGroupGetResponse studyGroupGetResponse = createStudyGroupGetResponse(memberDto, 1L);

        given(studyGroupService.getStudyGroup(anyLong()))
                .willReturn(studyGroupGetResponse);

        //when //then
        mockMvc.perform(get("/api/v1/study-group/{id}",1)
                )
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print())
        ;
    }


    @DisplayName("스터디 그룹을 검색 조건 및 페이징 조건과 함께 여러건 조회 할 수 있다.")
    @Test
    void getStudyGroupListSuccess() throws Exception {
        //given
        int page = 0;
        int size = 5;
        PageRequest pageRequest = PageRequest.of(page, size);

        MemberDto memberDto1 = createMemberDto(1L);
        MemberDto memberDto2 = createMemberDto(2L);
        MemberDto memberDto3 = createMemberDto(3L);
        MemberDto memberDto4 = createMemberDto(4L);
        MemberDto memberDto5 = createMemberDto(5L);
        StudyGroupGetResponse studyGroupGetResponse1 = createStudyGroupGetResponse(memberDto1, 1L);
        StudyGroupGetResponse studyGroupGetResponse2 = createStudyGroupGetResponse(memberDto1, 2L);
        StudyGroupGetResponse studyGroupGetResponse3 = createStudyGroupGetResponse(memberDto1, 3L);
        StudyGroupGetResponse studyGroupGetResponse4 = createStudyGroupGetResponse(memberDto1, 4L);
        StudyGroupGetResponse studyGroupGetResponse5 = createStudyGroupGetResponse(memberDto1, 5L);

        List<StudyGroupGetResponse> contents = List.of(studyGroupGetResponse1, studyGroupGetResponse2, studyGroupGetResponse3, studyGroupGetResponse4, studyGroupGetResponse5);

        ArgumentCaptor<StudyGroupSearchCond> searchCondCaptor = ArgumentCaptor.forClass(StudyGroupSearchCond.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        PageImpl<StudyGroupGetResponse> studyGroupGetResponses = new PageImpl<>(contents, pageRequest, 5L);
        given(studyGroupService.getStudyGroupList(pageableCaptor.capture(), searchCondCaptor.capture()))
                .willReturn(studyGroupGetResponses);

        //when //then
        mockMvc.perform(get("/api/v1/study-group")
                        .param("page","0")
                        .param("size","5")
                        .param("subject","일반 주제")
                        .param("leaderName","Kim")
                )
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andDo(print())
        ;

        Assertions.assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
        Assertions.assertThat(pageableCaptor.getValue().getOffset()).isEqualTo(0);
        Assertions.assertThat(searchCondCaptor.getValue().getLeaderName()).isEqualTo("Kim");
        Assertions.assertThat(searchCondCaptor.getValue().getSubject()).isEqualTo("일반 주제");
    }

    @DisplayName("스터디 그룹 여러 건 검색시 검색 조건 및 페이징 조건 없는 경우 page=0, size=10이 기본 값 으로 들어간다.")
    @Test
    void getStudyGroupListBasicSuccess() throws Exception {
        //given
        int page = 0;
        int size = 5;
        PageRequest pageRequest = PageRequest.of(page, size);

        MemberDto memberDto1 = createMemberDto(1L);
        MemberDto memberDto2 = createMemberDto(2L);
        MemberDto memberDto3 = createMemberDto(3L);
        MemberDto memberDto4 = createMemberDto(4L);
        MemberDto memberDto5 = createMemberDto(5L);
        StudyGroupGetResponse studyGroupGetResponse1 = createStudyGroupGetResponse(memberDto1, 1L);
        StudyGroupGetResponse studyGroupGetResponse2 = createStudyGroupGetResponse(memberDto1, 2L);
        StudyGroupGetResponse studyGroupGetResponse3 = createStudyGroupGetResponse(memberDto1, 3L);
        StudyGroupGetResponse studyGroupGetResponse4 = createStudyGroupGetResponse(memberDto1, 4L);
        StudyGroupGetResponse studyGroupGetResponse5 = createStudyGroupGetResponse(memberDto1, 5L);

        List<StudyGroupGetResponse> contents = List.of(studyGroupGetResponse1, studyGroupGetResponse2, studyGroupGetResponse3, studyGroupGetResponse4, studyGroupGetResponse5);

        ArgumentCaptor<StudyGroupSearchCond> searchCondCaptor = ArgumentCaptor.forClass(StudyGroupSearchCond.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        PageImpl<StudyGroupGetResponse> studyGroupGetResponses = new PageImpl<>(contents, pageRequest, 5L);
        given(studyGroupService.getStudyGroupList(pageableCaptor.capture(), searchCondCaptor.capture()))
                .willReturn(studyGroupGetResponses);

        //when //then
        mockMvc.perform(get("/api/v1/study-group")
                )
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andDo(print())
        ;

        Assertions.assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);   //기본 값 10
        Assertions.assertThat(pageableCaptor.getValue().getOffset()).isEqualTo(0);  //기본값 0
        Assertions.assertThat(searchCondCaptor.getValue().getLeaderName()).isNull();
        Assertions.assertThat(searchCondCaptor.getValue().getSubject()).isNull();
    }

    @DisplayName("스터디 그룹 여러 건 검색시 한번에 최대로 조회할 수 있는 값은 50개 이다.")
    @Test
    void getStudyGroupListLimitSuccess() throws Exception {
        //given
        int page = 0;
        int size = 5;
        PageRequest pageRequest = PageRequest.of(page, size);

        MemberDto memberDto1 = createMemberDto(1L);
        MemberDto memberDto2 = createMemberDto(2L);
        MemberDto memberDto3 = createMemberDto(3L);
        MemberDto memberDto4 = createMemberDto(4L);
        MemberDto memberDto5 = createMemberDto(5L);
        StudyGroupGetResponse studyGroupGetResponse1 = createStudyGroupGetResponse(memberDto1, 1L);
        StudyGroupGetResponse studyGroupGetResponse2 = createStudyGroupGetResponse(memberDto1, 2L);
        StudyGroupGetResponse studyGroupGetResponse3 = createStudyGroupGetResponse(memberDto1, 3L);
        StudyGroupGetResponse studyGroupGetResponse4 = createStudyGroupGetResponse(memberDto1, 4L);
        StudyGroupGetResponse studyGroupGetResponse5 = createStudyGroupGetResponse(memberDto1, 5L);

        List<StudyGroupGetResponse> contents = List.of(studyGroupGetResponse1, studyGroupGetResponse2, studyGroupGetResponse3, studyGroupGetResponse4, studyGroupGetResponse5);

        ArgumentCaptor<StudyGroupSearchCond> searchCondCaptor = ArgumentCaptor.forClass(StudyGroupSearchCond.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        PageImpl<StudyGroupGetResponse> studyGroupGetResponses = new PageImpl<>(contents, pageRequest, 5L);
        given(studyGroupService.getStudyGroupList(pageableCaptor.capture(), searchCondCaptor.capture()))
                .willReturn(studyGroupGetResponses);

        //when //then
        mockMvc.perform(get("/api/v1/study-group")
                        .param("size","10000")
                )
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andDo(print())
        ;

        Assertions.assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);   //최댜 size = 50
        Assertions.assertThat(pageableCaptor.getValue().getOffset()).isEqualTo(0);  //기본값 0
        Assertions.assertThat(searchCondCaptor.getValue().getLeaderName()).isNull();
        Assertions.assertThat(searchCondCaptor.getValue().getSubject()).isNull();
    }



    private StudyGroupGetResponse createStudyGroupGetResponse(MemberDto memberDto, long id) {
        return StudyGroupGetResponse.builder()
                .id(id)
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .recruitmentStartAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                .recruitmentEndAt(LocalDateTime.of(2023, 1, 1, 0, 0, 1))
                .studyStartAt(LocalDateTime.of(2023, 1, 1, 0, 0, 2))
                .studyEndAt(LocalDateTime.of(2023, 1, 1, 0, 0, 3))
                .maxSize(10)
                .price(10000L)
                .leader(memberDto)
                .build();
    }

    private MemberDto createMemberDto(long id) {
        return MemberDto.builder()
                .id(id)
                .email("email")
                .name("name")
                .phone("phone")
                .career("career")
                .point(30000L)
                .role(Role.MEMBER)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    private StudyGroupCreateRequest makeStudyGroupCreateRequest(String subject) {
        return StudyGroupCreateRequest.builder()
                .memberId(1L)
                .subject(subject)
                .contents("content_test")
                .contentsDetail("details_test")
                .maxSize(10)
                .price(25000L)
                .studyStartAt(LocalDateTime.of(2023, 10, 1, 10, 0, 0))
                .studyEndAt(LocalDateTime.of(2023, 11, 1, 10, 0, 0))
                .recruitmentEndAt(LocalDateTime.of(2023, 9, 30, 10, 0, 0))
                .recruitmentStartAt(LocalDateTime.of(2023, 9, 1, 10, 0, 0))
                .build();
    }
}