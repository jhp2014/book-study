package com.project.bookstudy.member.exception;

import com.project.bookstudy.member.dto.ErrorInfo;

import java.util.List;

public class MemberNotFound extends MemberException{

    public final static String MESSAGE = "잘못된 요청입니다.";

    public MemberNotFound(String field, String errorMessage) {
        super(MESSAGE);
        addErrorInfo(field, errorMessage);
    }

    @Override
    public int getErrorCode() {
        return 400;
    }

    @Override
    public void addErrorInfo(String field, String errorMessage) {
        super.addErrorInfo(field, errorMessage);
    }
}
