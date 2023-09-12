package com.project.bookstudy.board.repository.category;

import com.project.bookstudy.board.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {
}
