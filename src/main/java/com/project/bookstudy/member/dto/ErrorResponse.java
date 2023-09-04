package com.project.bookstudy.member.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ErrorResponse {

    private String code;
    private String message;
    private List<ErrorField> errorFieldList;

    @Builder
    private ErrorResponse(String code, String message, List<ErrorField> errorFieldList) {
        this.code = code;
        this.message = message;
        this.errorFieldList = errorFieldList != null ?
                new ArrayList<>(errorFieldList) :
                new ArrayList<>();
    }

    public void addErrorInfo(String field, String message) {
        errorFieldList.add(new ErrorField(field, message));
    }

    public List<ErrorField> getErrorFieldList() {
        return Collections.unmodifiableList(errorFieldList);
    }
}
