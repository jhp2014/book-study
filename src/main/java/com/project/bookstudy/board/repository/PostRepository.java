package com.project.bookstudy.board.repository;

import com.project.bookstudy.board.dmain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

}
