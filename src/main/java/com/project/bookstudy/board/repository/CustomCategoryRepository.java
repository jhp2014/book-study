package com.project.bookstudy.board.repository;

import com.project.bookstudy.board.dmain.Category;

import java.util.List;

public interface CustomCategoryRepository {

    List<Category> findRootOrChildByParentId(Long id);
}
