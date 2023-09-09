package com.project.bookstudy.board.repository;

import com.project.bookstudy.board.dmain.Post;
import com.project.bookstudy.board.dto.PostSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomPostRepository {

    Optional<Post> findByIdWithAll(Long id);

    Page<Post> searchPost(Pageable pageable, PostSearchCond cond);
}
