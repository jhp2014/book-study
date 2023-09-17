package com.project.bookstudy.comment.controller;

import com.project.bookstudy.comment.dto.*;
import com.project.bookstudy.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    @PostMapping
    public CreateCommentResponse createComment(@RequestBody CreateCommentRequest request) {
        CommentDto commentDto = commentService
                .createComment(request.getPostId(), request.getMemberId(), request.getParentId(), request.toCommentParam());

        return CreateCommentResponse.builder()
                .commentId(commentDto.getId())
                .build();
    }

    @PutMapping
    public void updateComment (@RequestBody UpdateCommentRequest request) {
        commentService.updateComment(request.getUpdateCommentParam());
    }

/*    @GetMapping("/{id}")
    public Page<CommentDto> getCommentList(@PageableDefault Pageable pageable,
                                                 @PathVariable("id") Long postId) {

        return commentService.getCommentList(pageable, postId);
    }*/

    @PatchMapping("/{id}")
    public void deleteComment(@PathVariable("id") Long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping
    public CommentResponse getRootOrChildComment(@RequestParam(required = false) Long parentId) {
        List<CommentDto> rootOrChildCommentList = commentService.getRootOrChildCommentList(parentId);

        return CommentResponse.builder()
                .commentId(parentId)
                .childComments(rootOrChildCommentList)
                .build();
    }

}
