package com.project.bookstudy.member.exception;

import com.project.bookstudy.member.dto.ErrorField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MemberException extends RuntimeException {


    private final List<ErrorField> errorFieldList = new ArrayList<>();

    public MemberException(String message) {
        super(message);
    }

    public abstract int getErrorCode();

    public void addErrorInfo(String field, String errorMessage) {
        errorFieldList.add(new ErrorField(field, errorMessage));
    }

    public List<ErrorField> getErrorInfoList() {
        return Collections.unmodifiableList(errorFieldList);
    }
}
