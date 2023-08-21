package com.project.bookstudy.member.exception;

public class DuplicateEmail extends MemberException {

    private static final String MESSAGE = "같은 이메일이 존재합니다.";

    public DuplicateEmail(String field, String errorMessage) {
        super(MESSAGE);
        addErrorInfo(field, errorMessage);
    }

    @Override
    public int getErrorCode() {
        return 400;
    }
}
