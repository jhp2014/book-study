package com.project.bookstudy.comment.repository;



import com.project.bookstudy.comment.domain.Comment;

import java.util.List;

public interface CustomCommentRepository {

    List<Comment> findRootOrChildByParentId(Long id);

}
