package com.project.bookstudy.board.repository.post;

import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

    List<Post> findPostsByCategory(Category category);

    @Query("select p from Post p left join fetch p.files where p.id = :id")
    Post findByIdFetchFiles(@Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.isDeleted = true where p.category = :category")
    int softDeleteAllByCategory(@Param("category") Category category);
}
