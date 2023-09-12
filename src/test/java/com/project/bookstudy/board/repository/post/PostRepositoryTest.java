package com.project.bookstudy.board.repository.post;

import com.project.bookstudy.TestDataProvider;
import com.project.bookstudy.board.domain.File;
import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.dto.post.CreatePostRequest;
import com.project.bookstudy.board.domain.Category;
import com.project.bookstudy.board.repository.category.CategoryRepository;
import com.project.bookstudy.board.repository.file.FileRepository;
import com.project.bookstudy.board.repository.post.PostRepository;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyGroupRepository studyGroupRepository;
    @Autowired
    FileRepository fileRepository;


    @DisplayName("카테고리를 통해 카테고리의 모든 게시글을 소프트 삭제를 할 수 있다.")
    @Test
    void softDeleteAllBy() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = StudyGroup.from(member, request.toStudyGroupParam());
        studyGroupRepository.save(studyGroup);

        Category category = Category.from(null, studyGroup, "부모카테고리");
        categoryRepository.save(category);

        CreatePostRequest postRequest1 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post1 = Post.of(postRequest1.getContents(), postRequest1.getSubject(), studyGroup, member, category);
        postRepository.save(post1);

        CreatePostRequest postRequest2= TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post2 = Post.of(postRequest2.getContents(), postRequest2.getSubject(), studyGroup, member, category);
        postRepository.save(post2);

        CreatePostRequest postRequest3 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post3 = Post.of(postRequest3.getContents(), postRequest3.getSubject(), studyGroup, member, category);
        postRepository.save(post3);

        //when
        int resultNum = postRepository.softDeleteAllByCategory(category);

        //then
        Assertions.assertThat(resultNum).isEqualTo(3);
        Assertions.assertThat(postRepository.findById(post1.getId()).isEmpty()).isTrue();
        Assertions.assertThat(postRepository.findById(post2.getId()).isEmpty()).isTrue();
        Assertions.assertThat(postRepository.findById(post3.getId()).isEmpty()).isTrue();
    }

    @DisplayName("게시글 삭제 시 모든 파일은 Hard delete 가 실행된다.")
    @Test
    void deletePost() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        CreateStudyGroupRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = StudyGroup.from(member, request.toStudyGroupParam());
        studyGroupRepository.save(studyGroup);

        Category category = Category.from(null, studyGroup, "부모카테고리");
        categoryRepository.save(category);

        CreatePostRequest postRequest1 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post1 = Post.of(postRequest1.getContents(), postRequest1.getSubject(), studyGroup, member, category);
        postRepository.save(post1);


        File file1 = File.of("/path1", post1);
        File file2 = File.of("/path2", post1);
        fileRepository.saveAll(List.of(file1, file2));

        //when
        postRepository.delete(post1);

        //then
        Assertions.assertThat(fileRepository.findById(file1.getId()).isEmpty()).isTrue();
        Assertions.assertThat(fileRepository.findById(file2.getId()).isEmpty()).isTrue();
    }
}