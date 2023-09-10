package com.project.bookstudy.board.service;

import com.project.bookstudy.board.dmain.Category;
import com.project.bookstudy.board.dmain.File;
import com.project.bookstudy.board.dmain.Post;
import com.project.bookstudy.board.dto.*;
import com.project.bookstudy.board.repository.CategoryRepository;
import com.project.bookstudy.board.repository.PostRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long createPost(CreatePostRequest request) {
        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));


        List<File> files = request.getFiles()
                .stream()
                .map(path -> File.of(path.getFilePath()))
                .collect(Collectors.toList());

        Post post = Post.of(request.getContents(), request.getSubject(), studyGroup, member, category)
                .addFiles(files);

        Post savePost = postRepository.save(post);

        return savePost.getId();
    }

    @Transactional
    public void updatePost(UpdatePostRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        post.update(category, request.getSubject(), request.getContents());
    }

    @Transactional  //미완성
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        //댓글 및 파일까지 다 삭제 되어야 한다. dfs 사용해야하는듯. 양방향 설정해서 cascade로 삭제해도 되지만, batch로 삭제하자

        postRepository.delete(post);    //cascade test
    }

    public PostDto getPost(Long postId) {
        Post post = postRepository.findByIdWithAll(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        PostDto postDto = PostDto.fromEntity(post);
        return postDto;
    }

    public Page<PostDto> getPostList(Pageable pageable, PostSearchCond cond) {
        Page<PostDto> postDtos = postRepository.searchPost(pageable, cond)
                .map(PostDto::fromEntity);
        return postDtos;
    }

}
