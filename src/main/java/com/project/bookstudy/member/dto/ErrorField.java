package com.project.bookstudy.member.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorField {
    private String field;
    private String errorMessage;

    @Builder
    public ErrorField(String field, String errorMessage) {
        this.field = field;
        this.errorMessage = errorMessage;
    }
}
