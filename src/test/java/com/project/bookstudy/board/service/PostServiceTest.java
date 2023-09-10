package com.project.bookstudy.board.service;

import com.project.bookstudy.board.dmain.Category;
import com.project.bookstudy.board.dmain.Post;
import com.project.bookstudy.board.dto.CreateCategoryRequest;
import com.project.bookstudy.board.dto.CreatePostRequest;
import com.project.bookstudy.board.repository.CategoryRepository;
import com.project.bookstudy.board.repository.FileRepository;
import com.project.bookstudy.board.repository.PostRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.study_group.service.StudyGroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    StudyGroupService studyGroupService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FileRepository fileRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    StudyGroupRepository studyGroupRepository;

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;

    @Test
    void createPostSuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");
        StudyGroupDto studyGroup = studyGroupService.createStudyGroup(request.getMemberId(), request.toStudyGroupParam());
        StudyGroup findStudGroup = studyGroupRepository.findById(studyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        CreateCategoryRequest categoryRequest = CreateCategoryRequest.builder()
                .subject("subject")
                .studyGroupId(studyGroup.getId())
                .build();
        Long categoryId = categoryService.createCategory(categoryRequest);
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        CreatePostRequest postRequest = CreatePostRequest.builder()
                .categoryId(categoryId)
                .studyGroupId(studyGroup.getId())
                .memberId(member.getId())
                .contents("test_content")
                .subject("test_subject")
                .build();

        Long postId = postService.createPost(postRequest);
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findPost.getSubject()).isEqualTo(postRequest.getSubject());
        assertThat(findPost.getContents()).isEqualTo(postRequest.getContents());
        assertThat(findPost.getMember().getId()).isEqualTo(postRequest.getMemberId());
        assertThat(findPost.getStudyGroup().getId()).isEqualTo(postRequest.getStudyGroupId());
    }

    @Test
    void deletePostSuccess() {
        //given
        Member member = memberRepository.save(createMember("박종훈"));
        CreateStudyGroupRequest request = getStudyGroupRequest(member, "test");
        StudyGroupDto studyGroup = studyGroupService.createStudyGroup(request.getMemberId(), request.toStudyGroupParam());
        StudyGroup findStudGroup = studyGroupRepository.findById(studyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        CreateCategoryRequest categoryRequest = CreateCategoryRequest.builder()
                .subject("subject")
                .studyGroupId(studyGroup.getId())
                .build();
        Long categoryId = categoryService.createCategory(categoryRequest);
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        CreatePostRequest postRequest = CreatePostRequest.builder()
                .categoryId(categoryId)
                .studyGroupId(studyGroup.getId())
                .memberId(member.getId())
                .contents("test_content")
                .subject("test_subject")
                .build();
        Post post = Post.of(postRequest.getContents(),
                postRequest.getSubject(), findStudGroup, member, findCategory);
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

}