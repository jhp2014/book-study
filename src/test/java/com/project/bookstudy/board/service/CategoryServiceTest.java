package com.project.bookstudy.board.service;

import com.project.bookstudy.board.dmain.Category;
import com.project.bookstudy.board.dto.CreateCategoryRequest;
import com.project.bookstudy.board.repository.CategoryRepository;
import com.project.bookstudy.board.repository.FileRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.study_group.service.StudyGroupService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;

    @Autowired
    StudyGroupRepository studyGroupRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("부모 카테고리 생성 성공")
    @Transactional
    void createParentCategorySuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));

        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        CreateCategoryRequest categoryRequest = getCreateCategoryRequest(1L, studyGroup);
        //when

        Long categoryId = categoryService.createCategory(categoryRequest);

        //then
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Assertions.assertThat(findCategory).isNotNull();
        Assertions.assertThat(findCategory.getSubject()).isEqualTo(categoryRequest.getSubject());
        Assertions.assertThat(findCategory.getStudyGroup().getId()).isEqualTo(categoryRequest.getStudyGroupId());
    }

    @Test
    @DisplayName("자식 카테고리 생성 성공")
    @Transactional
    void createChildCategorySuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));

        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        CreateCategoryRequest categoryRequest = getCreateCategoryRequest(1L, studyGroup);
        Category parentCategory = Category.from(null, studyGroup, categoryRequest.getSubject());
        categoryRepository.save(parentCategory);

        CreateCategoryRequest createChildCategoryRequest = CreateCategoryRequest.builder()
                .parentCategoryId(parentCategory.getId())
                .studyGroupId(studyGroup.getId())
                .subject("하위 주제")
                .build();

        
        //when

        Long categoryId = categoryService.createCategory(createChildCategoryRequest);

        //then
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Assertions.assertThat(findCategory).isNotNull();
        Assertions.assertThat(findCategory.getParentCategory().getId()).isEqualTo(parentCategory.getId());
        Assertions.assertThat(findCategory.getSubject()).isEqualTo(createChildCategoryRequest.getSubject());
        Assertions.assertThat(findCategory.getStudyGroup().getId()).isEqualTo(createChildCategoryRequest.getStudyGroupId());
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

    private CreateCategoryRequest getCreateCategoryRequest(Long parentId, StudyGroup studyGroup) {
        return CreateCategoryRequest.builder()
                .parentCategoryId(parentId)
                .subject("subject")
                .studyGroupId(studyGroup.getId())
                .build();
    }


}