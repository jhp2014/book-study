package com.project.bookstudy.board.controller;

import com.project.bookstudy.board.dto.post.PostDto;
import com.project.bookstudy.board.dto.post.CreatePostRequest;
import com.project.bookstudy.board.dto.post.PostSearchCond;
import com.project.bookstudy.board.dto.post.UpdatePostRequest;
import com.project.bookstudy.board.dto.file.*;
import com.project.bookstudy.board.dto.post.CreatePostResponse;
import com.project.bookstudy.board.service.FileService;
import com.project.bookstudy.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final FileService fileService;

    @PostMapping
    public CreatePostResponse createPost(@RequestBody CreatePostRequest request) {
        Long postId = postService.createPost(request);

        //파일 존재하면, 파일 업로드
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            fileService.upload(postId, request.getFiles());
        }

        return CreatePostResponse.builder()
                .postId(postId)
                .build();
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable("id") Long postId) {
        PostDto postDto = postService.getPost(postId);
        return postDto;
    }

    @GetMapping
    public Page<PostDto> getPosts(@PageableDefault Pageable pageable,
                         @ModelAttribute PostSearchCond cond) {
        Page<PostDto> postList = postService.getPostList(pageable, cond);
        return postList;
    }

    @PutMapping
    public void updatePost(@RequestBody UpdatePostRequest request) {
        postService.updatePost(request);
    }

    @PatchMapping("/file")
    public void uploadFiles(@RequestBody UploadFilesRequest request) {
        fileService.upload(request.getPostId(), request.getPath());
    }

    @DeleteMapping
    public void deleteFiles(@RequestBody DeleteFilesRequest request) {
        fileService.delete(request);
    }
}
