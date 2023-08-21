package com.project.bookstudy.member.exception;

import com.project.bookstudy.member.dto.ErrorInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MemberException extends RuntimeException {


    private final List<ErrorInfo> errorInfoList = new ArrayList<>();

    public MemberException(String message) {
        super(message);
    }

    public abstract int getErrorCode();

    public void addErrorInfo(String field, String errorMessage) {
        errorInfoList.add(new ErrorInfo(field, errorMessage));
    }

    public List<ErrorInfo> getErrorInfoList() {
        return Collections.unmodifiableList(errorInfoList);
    }
}
