package com.project.bookstudy.board.service;

import com.project.bookstudy.board.dto.post.CreatePostRequest;
import com.project.bookstudy.board.dto.post.PostDto;
import com.project.bookstudy.board.dto.post.PostSearchCond;
import com.project.bookstudy.board.dto.post.UpdatePostRequest;
import com.project.bookstudy.board.repository.file.FileRepository;
import com.project.bookstudy.board.domain.Category;
import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.repository.category.CategoryRepository;
import com.project.bookstudy.board.repository.post.PostRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    @Transactional
    public Long createPost(CreatePostRequest request) {
        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Post post = Post.of(request.getContents(), request.getSubject(), studyGroup, member, category);

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

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

//        fileRepository.deleteAllInBatch(post.getFiles());
        fileRepository.deleteAllInBatchByPostIn(List.of(post));
        postRepository.delete(post);
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
