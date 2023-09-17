package com.project.bookstudy.board.service;

import com.project.bookstudy.TestDataProvider;
import com.project.bookstudy.board.domain.File;
import com.project.bookstudy.board.domain.Category;
import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.dto.post.CreatePostRequest;
import com.project.bookstudy.board.dto.post.PostDto;
import com.project.bookstudy.board.repository.category.CategoryRepository;
import com.project.bookstudy.board.repository.file.FileRepository;
import com.project.bookstudy.board.repository.post.PostRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.api.controller.request.StudyGroupCreateRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostServiceTest {

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
    @Autowired
    EntityManager entityManager;


    @Test
    @Transactional
    void 게시글_생성_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        StudyGroupCreateRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toCreateServiceParam()));

        Category category = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        CreatePostRequest postRequest = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);

        //when
        Long postId = postService.createPost(postRequest);

        //then
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findPost.getSubject()).isEqualTo(postRequest.getSubject());
        assertThat(findPost.getContents()).isEqualTo(postRequest.getContents());
        assertThat(findPost.getMember().getId()).isEqualTo(postRequest.getMemberId());
        assertThat(findPost.getStudyGroup().getId()).isEqualTo(postRequest.getStudyGroupId());
        assertThat(findPost.getCategory().getId()).isEqualTo(postRequest.getCategoryId());
//        assertThat(findPost.getFiles().size()).isEqualTo(postRequest.getFiles().size());
    }

    @Test
    @Transactional
    void 게시글_조회_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        StudyGroupCreateRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toCreateServiceParam()));

        Category category = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        CreatePostRequest postRequest = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);

        Post post = Post.of(postRequest.getContents(), postRequest.getSubject(), studyGroup, member, category);
        postRepository.save(post);

        entityManager.flush();

        //when
        PostDto postDto = postService.getPost(post.getId());

        //then
        Post findPost = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findPost.getSubject()).isEqualTo(postRequest.getSubject());
        assertThat(findPost.getContents()).isEqualTo(postRequest.getContents());
        assertThat(findPost.getMember().getId()).isEqualTo(postRequest.getMemberId());
        assertThat(findPost.getStudyGroup().getId()).isEqualTo(postRequest.getStudyGroupId());
        assertThat(findPost.getCategory().getId()).isEqualTo(postRequest.getCategoryId());
    }


    @Test
    @DisplayName("Post 삭제 시 File 도 모두 삭제되어야 한다.")
    @Transactional
    void deletePostSuccess() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        StudyGroupCreateRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = StudyGroup.from(member, request.toCreateServiceParam());
        studyGroupRepository.save(studyGroup);

        Category category = Category.from(null, studyGroup, "부모카테고리");
        categoryRepository.save(category);

        CreatePostRequest postRequest1 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post = Post.of(postRequest1.getContents(), postRequest1.getSubject(), studyGroup, member, category);
        postRepository.save(post);

        File file1 = File.of("/path1", post);
        File file2 = File.of("/path2", post);
        File file3 = File.of("/path3", post);
        fileRepository.saveAll(List.of(file1, file2, file3));

        entityManager.flush();
        entityManager.clear();

        //when
        postService.deletePost(post.getId());

        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(fileRepository.findById(file1.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file2.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file3.getId()).isEmpty()).isTrue();
        assertThat(postRepository.findById(post.getId()).isEmpty()).isTrue();
    }
}