package com.project.bookstudy.board.dto;

import com.project.bookstudy.board.dmain.BaseTimeEntity;
import com.project.bookstudy.board.dmain.Category;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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
