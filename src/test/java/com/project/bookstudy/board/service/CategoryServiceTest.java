package com.project.bookstudy.board.service;

import com.project.bookstudy.TestDataProvider;
import com.project.bookstudy.board.domain.File;
import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.dto.post.CreatePostRequest;
import com.project.bookstudy.board.repository.file.FileRepository;
import com.project.bookstudy.board.repository.post.PostRepository;
import com.project.bookstudy.board.domain.Category;
import com.project.bookstudy.board.dto.category.CreateCategoryRequest;
import com.project.bookstudy.board.repository.category.CategoryRepository;
import com.project.bookstudy.board.service.CategoryService;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager entityManager;


    @Test
    @Transactional
    void 부모카테고리_생성_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        CreateCategoryRequest categoryRequest = TestDataProvider.makeCreateCategoryRequest(null, studyGroup);

        //when
        Long categoryId = categoryService.createCategory(categoryRequest);

        //then
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findCategory).isNotNull();
        assertThat(findCategory.getParentCategory()).isNull();
        assertThat(findCategory.getSubject()).isEqualTo(categoryRequest.getSubject());
        assertThat(findCategory.getStudyGroup().getId()).isEqualTo(categoryRequest.getStudyGroupId());
    }

    @Test
    @Transactional
    void 자식카테고리_생성_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        CreateCategoryRequest categoryRequest = TestDataProvider.makeCreateCategoryRequest(parentCategory.getId(), studyGroup);

        //when
        Long categoryId = categoryService.createCategory(categoryRequest);

        //then
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        assertThat(findCategory.getId()).isEqualTo(categoryId);
        assertThat(findCategory.getParentCategory().getId()).isEqualTo(parentCategory.getId());
        assertThat(findCategory.getSubject()).isEqualTo(categoryRequest.getSubject());
        assertThat(findCategory.getIsDeleted()).isEqualTo(Boolean.FALSE);
        assertThat(parentCategory.getChildCategories().contains(findCategory)).isTrue();
    }

    @Test
    @Transactional
    void 카테고리_삭제_성공() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.from(member, request.toStudyGroupParam()));

        Category category1 = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));
        Category category2 = categoryRepository.save(Category.from(category1, studyGroup, "두번째 카테고리"));
        Category category3 = categoryRepository.save(Category.from(category2, studyGroup, "세번째 카테고리"));

        CreatePostRequest postRequest1 = TestDataProvider.makeCreatePostRequest(category1, member, studyGroup);
        Post post1 = Post.of(postRequest1.getContents(), postRequest1.getSubject(), studyGroup, member, category1);
        postRepository.save(post1);

        CreatePostRequest postRequest2= TestDataProvider.makeCreatePostRequest(category2, member, studyGroup);
        Post post2 = Post.of(postRequest2.getContents(), postRequest2.getSubject(), studyGroup, member, category2);
        postRepository.save(post2);

        CreatePostRequest postRequest3 = TestDataProvider.makeCreatePostRequest(category3, member, studyGroup);
        Post post3 = Post.of(postRequest3.getContents(), postRequest3.getSubject(), studyGroup, member, category3);
        postRepository.save(post3);

        CreatePostRequest postRequest4 = TestDataProvider.makeCreatePostRequest(category1, member, studyGroup);
        Post post4 = Post.of(postRequest4.getContents(), postRequest4.getSubject(), studyGroup, member, category1);
        postRepository.save(post4);

        File file1 = File.of("/path1", post1);
        File file2 = File.of("/path2", post1);
        File file3 = File.of("/path3", post1);
        fileRepository.saveAll(List.of(file1, file2, file3));

        File file4 = File.of("/path1", post4);
        File file5 = File.of("/path2", post4);
        File file6 = File.of("/path3", post4);
        fileRepository.saveAll(List.of(file4, file5, file6));

        entityManager.flush();
        entityManager.clear();

        System.out.println(">>>>>>>>>>>>>>>>>> delete Start");
        //when
        categoryService.deleteCategory(category1.getId());

        entityManager.flush();
        entityManager.clear();
        System.out.println(">>>>>>>>>>>>>>>>>> delete end");

        //then
        assertThat(postRepository.findById(post1.getId()).isEmpty()).isTrue();
        assertThat(postRepository.findById(post2.getId()).isEmpty()).isTrue();
        assertThat(postRepository.findById(post3.getId()).isEmpty()).isTrue();
        assertThat(postRepository.findById(post4.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file1.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file2.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file3.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file4.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file5.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file6.getId()).isEmpty()).isTrue();
    }
}


