package com.project.bookstudy.board.dto.category;

import com.project.bookstudy.board.domain.Category;
import lombok.*;

@Getter
public class CategoryDto{

    private Long id;
    private String subject;

    @Builder
    private CategoryDto(Long id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    public static CategoryDto fromEntity(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .subject(category.getSubject())
                .build();
    }
}
