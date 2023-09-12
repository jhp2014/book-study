package com.project.bookstudy.board.dto.post;

import com.project.bookstudy.board.domain.Post;
import com.project.bookstudy.board.dto.file.FileDto;
import com.project.bookstudy.board.dto.category.CategoryDto;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostDto {

    private Long id;
    private String subject;
    private String contents;
    private CategoryDto categoryDto;
    private MemberDto memberDto;
    private StudyGroupDto studyGroupDto;

    private List<FileDto> filePaths;

    @Builder(access = AccessLevel.PRIVATE)
    public PostDto(Long id, String subject, String contents, CategoryDto categoryDto, MemberDto memberDto, StudyGroupDto studyGroupDto, List<FileDto> filePaths) {
        this.id = id;
        this.subject = subject;
        this.contents = contents;
        this.categoryDto = categoryDto;
        this.memberDto = memberDto;
        this.studyGroupDto = studyGroupDto;
        this.filePaths = filePaths;
    }

    @Builder(access = AccessLevel.PRIVATE)


    public static PostDto fromEntity(Post post) {
        //만약 post에 Proxy 객체가 들어가있다면? → 어차피 Dto 생성 과정에서 다 Lazy Loading 된다.
        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .contents(post.getContents())
                .categoryDto(CategoryDto.fromEntity(post.getCategory()))
                .studyGroupDto(StudyGroupDto.fromEntity(post.getStudyGroup()))
                .filePaths(post.getFiles()
                        .stream()
                        .map(FileDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
        return postDto;
    }
}