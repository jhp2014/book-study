package com.project.bookstudy.board.dto.file;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DeleteFilesRequest {

    private List<Long> fileIds;
}
