package com.project.bookstudy.board.dto.file;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateFileRequest {
    private String filePath;
    public CreateFileRequest(String filePath) {
        this.filePath = filePath;
    }

}
