package com.project.bookstudy.common.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResonse<T> {

    private int code;
    private HttpStatus status;
    private String message;
    private T data;

    private ApiResonse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResonse<T> of(HttpStatus status, String message, T data) {
        return new ApiResonse<>(status, message, data);
    }

    public static <T> ApiResonse<T> of(HttpStatus status, T data) {
        return of(status, status.name(), data);
    }

    public static <T> ApiResonse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }


}
