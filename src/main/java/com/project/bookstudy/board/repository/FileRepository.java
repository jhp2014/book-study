package com.project.bookstudy.board.repository;

import com.project.bookstudy.board.dmain.File;
import com.project.bookstudy.board.dmain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long>, CustomFileRepository {

    List<File> findAllByPost(Post post);
}
