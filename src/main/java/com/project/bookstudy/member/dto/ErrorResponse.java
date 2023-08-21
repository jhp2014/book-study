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
    private List<ErrorInfo> errorInfoList;

    @Builder
    private ErrorResponse(String code, String message, List<ErrorInfo> errorInfoList) {
        this.code = code;
        this.message = message;
        this.errorInfoList = errorInfoList != null ?
                new ArrayList<>(errorInfoList) :
                new ArrayList<>();
    }

    public void addErrorInfo(String field, String message) {
        errorInfoList.add(new ErrorInfo(field, message));
    }

    public List<ErrorInfo> getErrorInfoList() {
        return Collections.unmodifiableList(errorInfoList);
    }
}
