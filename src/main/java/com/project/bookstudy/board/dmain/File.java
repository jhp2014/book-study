package com.project.bookstudy.board.dmain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Boolean isDeleted = Boolean.FALSE;

    @Builder
    private File(String filePath, Post post) {
        this.filePath = filePath;
        this.post = post;
    }

    public static File of(String filePath) {
        File file = File.builder()
                .filePath(filePath)
                .build();

        return file;
    }

    //양방향 연관관계 세팅을 위한 Setter
    public void setPost(Post post) {
        this.post = post;
    }
}
