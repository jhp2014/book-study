package com.project.bookstudy.board.dto;

import lombok.Getter;

@Getter
public class CreateAndUpdateFileRequest {
    private String filePath;

    public CreateAndUpdateFileRequest(String filePath) {
        this.filePath = filePath;
    }
}
