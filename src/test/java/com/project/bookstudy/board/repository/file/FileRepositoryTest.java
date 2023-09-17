package com.project.bookstudy.board.repository.file;

import com.project.bookstudy.TestDataProvider;
import com.project.bookstudy.board.domain.File;
import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.dto.post.CreatePostRequest;
import com.project.bookstudy.board.domain.Category;
import com.project.bookstudy.board.repository.category.CategoryRepository;
import com.project.bookstudy.board.repository.post.PostRepository;
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
class FileRepositoryTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    StudyGroupRepository studyGroupRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("deleteAllInBatch를 통해 파일을 한번에 삭제할 수 있다.")
    void deleteAllInBatch() {
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

        //when
        fileRepository.deleteAllInBatch(List.of(file1, file2, file3));

        //then
        assertThat(fileRepository.findById(file1.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file2.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file3.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("deleteAllInBatch에 Proxy List를 전달해 File들을 한번에 삭제할 수 있다.")
    @Transactional
    void deleteAllInBatchWithProxy() {
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

        Post post1 = postRepository.findById(post.getId()).get();

        //when
        fileRepository.deleteAllInBatch(post1.getFiles());

        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(fileRepository.findById(file1.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file2.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file3.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("PostId 값들은 전달해서 File을 한번에 삭제할 수 있다.")
    @Transactional
    void deleteAllinBatchFromPost() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        StudyGroupCreateRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = StudyGroup.from(member, request.toCreateServiceParam());
        studyGroupRepository.save(studyGroup);

        Category category = Category.from(null, studyGroup, "부모카테고리");
        categoryRepository.save(category);

        CreatePostRequest postRequest1 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post1 = Post.of(postRequest1.getContents(), postRequest1.getSubject(), studyGroup, member, category);
        postRepository.save(post1);

        CreatePostRequest postRequest2 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post2 = Post.of(postRequest2.getContents(), postRequest2.getSubject(), studyGroup, member, category);
        postRepository.save(post2);

        File file1 = File.of("/path1", post1);
        File file2 = File.of("/path2", post1);
        File file3 = File.of("/path3", post2);
        File file4 = File.of("/path4", post2);
        fileRepository.saveAll(List.of(file1, file2, file3, file4));

        entityManager.flush();
        entityManager.clear();

        //when
        fileRepository.deleteAllInBatchByPostIn(List.of(post1, post2));

        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(fileRepository.findById(file1.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file2.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file3.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file4.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("PostId를 통해 deleteAll → 하지만, delete query가 하나하나씩 나간다.")
    void deleteAllByPost() {
        //given
        Member member = TestDataProvider.makeMember("박종훈1");
        memberRepository.save(member);

        StudyGroupCreateRequest request = TestDataProvider.makeCreateStudyGroupRequest(member, "스터디 주제");
        StudyGroup studyGroup = StudyGroup.from(member, request.toCreateServiceParam());
        studyGroupRepository.save(studyGroup);

        Category category = Category.from(null, studyGroup, "부모카테고리");
        categoryRepository.save(category);

        CreatePostRequest postRequest1 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post1 = Post.of(postRequest1.getContents(), postRequest1.getSubject(), studyGroup, member, category);
        postRepository.save(post1);

        CreatePostRequest postRequest2 = TestDataProvider.makeCreatePostRequest(category, member, studyGroup);
        Post post2 = Post.of(postRequest2.getContents(), postRequest2.getSubject(), studyGroup, member, category);
        postRepository.save(post2);

        File file1 = File.of("/path1", post1);
        File file2 = File.of("/path2", post1);
        File file3 = File.of("/path3", post2);
        File file4 = File.of("/path4", post2);
        fileRepository.saveAll(List.of(file1, file2, file3, file4));

        //when
        fileRepository.deleteAllByPostIn(List.of(post1, post2));

        //them
        assertThat(fileRepository.findById(file1.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file2.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file3.getId()).isEmpty()).isTrue();
        assertThat(fileRepository.findById(file4.getId()).isEmpty()).isTrue();
    }
}