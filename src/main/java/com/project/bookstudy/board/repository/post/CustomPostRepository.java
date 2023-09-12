package com.project.bookstudy.board.repository.post;

import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.dto.post.PostSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomPostRepository {
    Optional<Post> findByIdWithAll(Long id);
    Page<Post> searchPost(Pageable pageable, PostSearchCond cond);
}
