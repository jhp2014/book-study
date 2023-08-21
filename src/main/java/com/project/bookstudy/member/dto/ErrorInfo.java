package com.project.bookstudy.member.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorInfo {
    private String field;
    private String errorMessage;

    @Builder
    public ErrorInfo(String field, String errorMessage) {
        this.field = field;
        this.errorMessage = errorMessage;
    }
}
