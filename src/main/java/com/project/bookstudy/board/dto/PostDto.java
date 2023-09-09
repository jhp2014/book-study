package com.project.bookstudy.board.dto;

import com.project.bookstudy.board.dmain.Post;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDto {

    private Long id;
    private String subject;
    private String contents;
    private CategoryDto categoryDto;
    private MemberDto memberDto;
    private StudyGroupDto studyGroupDto;


    @Builder(access = AccessLevel.PRIVATE)
    private PostDto(Long id, String subject, String contents, CategoryDto categoryDto, MemberDto memberDto, StudyGroupDto studyGroupDto) {
        this.id = id;
        this.subject = subject;
        this.contents = contents;
        this.categoryDto = categoryDto;
        this.memberDto = memberDto;
        this.studyGroupDto = studyGroupDto;
    }

    public static PostDto fromEntity(Post post) {
        //만약 post에 Proxy 객체가 들어가있다면? → 어차피 Dto 생성 과정에서 다 Lazy Loading 된다.
        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .contents(post.getContents())
                .categoryDto(CategoryDto.fromEntity(post.getCategory()))
                .studyGroupDto(StudyGroupDto.fromEntity(post.getStudyGroup()))
                .build();
        return postDto;
    }
}