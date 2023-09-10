package com.project.bookstudy.board.service;

import com.project.bookstudy.board.dmain.Category;
import com.project.bookstudy.board.dmain.File;
import com.project.bookstudy.board.dmain.Post;
import com.project.bookstudy.board.dto.CreatePostRequest;
import com.project.bookstudy.board.dto.PostDto;
import com.project.bookstudy.board.dto.PostSearchCond;
import com.project.bookstudy.board.dto.UpdatePostRequest;
import com.project.bookstudy.board.repository.CategoryRepository;
import com.project.bookstudy.board.repository.FileRepository;
import com.project.bookstudy.board.repository.PostRepository;
import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;


}
