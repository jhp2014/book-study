package com.project.bookstudy.board.repository;

import com.project.bookstudy.board.dmain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {
}
