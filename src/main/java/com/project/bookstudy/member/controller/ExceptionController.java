package com.project.bookstudy.member.controller;

import com.project.bookstudy.member.dto.ErrorResponse;
import com.project.bookstudy.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message("잘못된 입력입니다.")
                .build();

        e.getFieldErrors()
                .stream()
                .forEach(error -> {
                    errorResponse.addErrorInfo(error.getField(), error.getDefaultMessage());
                });

        return errorResponse;
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> memberExceptionHandler(MemberException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(e.getErrorCode()))
                .message("잘못된 요청입니다.")
                .errorInfoList(e.getErrorInfoList())
                .build();

        return ResponseEntity
                .status(e.getErrorCode())
                .body(errorResponse);
    }
}
