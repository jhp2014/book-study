package com.project.bookstudy.board.service;

import com.project.bookstudy.board.dmain.Category;
import com.project.bookstudy.board.dto.CategoryDto;
import com.project.bookstudy.board.dto.CreateCategoryRequest;
import com.project.bookstudy.board.dto.UpdateCategoryRequest;
import com.project.bookstudy.board.repository.CategoryRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Transactional
    public Long createCategory(CreateCategoryRequest request) {
        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Long parentId = request.getParentCategoryId();
        Category parentCategory = parentId != null ?
                categoryRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()))
                : null;

        Category category = Category.from(parentCategory, studyGroup, request.getSubject());
        categoryRepository.save(category);

        return category.getId();
    }

    //null 입력시 root Category List 반환
    public List<CategoryDto> getRootOrChildCategoryList(@Nullable Long parentId) {
        List<Category> rootOrChildCategories = categoryRepository.findRootOrChildByParentId(parentId);
        List<CategoryDto> categoryDtoList = rootOrChildCategories
                .stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());

        return categoryDtoList;
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        categoryRepository.delete(category);

        //해당 카테고리의 모든 게시물도 삭제 되어야 한다.
        //dfs 사용해야 할듯
    }

    @Transactional
    public void updateCategory(UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        category.update(request.getSubject(), toUpdateParentCategory(request));
    }

    private Category toUpdateParentCategory(UpdateCategoryRequest request) {
        if (request.getParentCategoryId() == null) return null;

        Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        return parentCategory;
    }
}
