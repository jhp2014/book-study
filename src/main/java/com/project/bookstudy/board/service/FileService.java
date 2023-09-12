package com.project.bookstudy.board.service;

import com.project.bookstudy.board.domain.File;
import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.dto.file.CreateFileRequest;
import com.project.bookstudy.board.dto.file.DeleteFilesRequest;
import com.project.bookstudy.board.dto.file.FileDto;
import com.project.bookstudy.board.repository.file.FileRepository;
import com.project.bookstudy.board.repository.post.PostRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    public void upload(Long PostId, List<CreateFileRequest> requests) {
        Post post = postRepository.findById(PostId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        List<File> files = requests.stream()
                .map(f -> File.of(f.getFilePath(), post))
                .collect(Collectors.toList());

        fileRepository.saveAll(files);
    }

    public void delete(DeleteFilesRequest request) {
        fileRepository.deleteAllByIdInBatch(request.getFileIds());
    }

    public List<FileDto> getFilesByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));
        List<FileDto> fileDtoList = fileRepository.findAllByPost(post)
                .stream()
                .map(FileDto::fromEntity)
                .collect(Collectors.toList());
        return fileDtoList;
    }
}
