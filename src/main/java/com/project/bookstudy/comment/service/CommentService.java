package com.project.bookstudy.comment.service;


import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.repository.post.PostRepository;
import com.project.bookstudy.comment.domain.Comment;
import com.project.bookstudy.comment.domain.param.CreateCommentParam;
import com.project.bookstudy.comment.domain.param.UpdateCommentParam;
import com.project.bookstudy.comment.dto.CommentDto;
import com.project.bookstudy.comment.repository.CommentRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 최상단 댓글은 parentId에 0 넣기?
    @Transactional
    public CommentDto createComment(Long postId, Long memberId, Long parentId, CreateCommentParam commentParam) {

         Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Comment comment = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Comment savedComment = commentRepository.save(Comment.from(post, member, comment, commentParam));
        CommentDto commentDto = CommentDto.fromEntity(savedComment);
        return commentDto;

    }

    @Transactional
    public void updateComment(UpdateCommentParam updateParam) {
        Comment comment = commentRepository.findById(updateParam.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        comment.update(updateParam);
    }


    /*public Page<CommentDto> getCommentList(Pageable pageable, Long postId) {

        Page<Comment> comments = commentRepository.findByPostId(pageable, postId);
        Page<CommentDto> commentDtoList = comments
                .map(comment -> CommentDto.fromEntity(comment));

        return commentDtoList;
    }*/

    public List<CommentDto> getRootOrChildCommentList(@Nullable Long parentId) {
        List<Comment> rootOrChildComments = commentRepository.findRootOrChildByParentId(parentId);
        List<CommentDto> commentDtoList = rootOrChildComments
                .stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());

        return commentDtoList;
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        if (comment.getIsDeleted()) {
            throw new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage());
        }
        //comment.delete();
        deleteChildCommentAndParentComment(comment);
    }

    private void deleteChildCommentAndParentComment(Comment comment) {

        if (comment == null) {
            return;
        }
        List<Comment> childComments = commentRepository.findRootOrChildByParentId(comment.getId());
        for (Comment childComment : childComments) {
            deleteChildCommentAndParentComment(childComment);
        }

        commentRepository.delete(comment);
    }


}
