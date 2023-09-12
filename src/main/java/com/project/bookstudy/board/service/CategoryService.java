package com.project.bookstudy.board.service;

import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.repository.file.FileRepository;
import com.project.bookstudy.board.repository.post.PostRepository;
import com.project.bookstudy.board.domain.Category;
import com.project.bookstudy.board.dto.category.CategoryDto;
import com.project.bookstudy.board.dto.category.CreateCategoryRequest;
import com.project.bookstudy.board.dto.category.UpdateCategoryRequest;
import com.project.bookstudy.board.repository.category.CategoryRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;

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

        //해당 카테고리의 모든 게시물도 삭제 되어야 한다.
        //dfs 사용해야 할듯
        deleteRelatedData(category);
    }

    private void deleteRelatedData(Category category) {

        if (category == null) return;

        List<Category> childCategories = categoryRepository.findRootOrChildByParentId(category.getId());
        for (Category childCategory : childCategories) {
            deleteRelatedData(childCategory);
        }

        List<Post> postList = postRepository.findPostsByCategory(category);

        //게시판 파일 삭제
        fileRepository.deleteAllInBatchByPostIn(postList);
        //카테고리의 게시판 삭제
        postRepository.softDeleteAllByCategory(category);
        //카테고리 삭제
        categoryRepository.delete(category);
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
