package com.project.bookstudy.board.repository.category;

import com.project.bookstudy.board.domain.Category;

import java.util.List;

public interface CustomCategoryRepository {

    List<Category> findRootOrChildByParentId(Long id);

}
