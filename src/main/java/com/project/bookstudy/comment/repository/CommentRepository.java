package com.project.bookstudy.comment.repository;

import com.project.bookstudy.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {
    Page<Comment> findByPostId(Pageable pageable, Long postId);
}
