package com.project.bookstudy.comment.repository;

import com.project.bookstudy.comment.domain.Comment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.project.bookstudy.comment.domain.QComment.comment;


@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findRootOrChildByParentId(Long id) {

        List<Comment> childCommentList = jpaQueryFactory.selectFrom(comment)
                .where(getRootOrChild(id))
                .fetch();

        return childCommentList;
    }

    private BooleanExpression getRootOrChild(Long id) {
        return id != null ? comment.parent.id.eq(id) : null;
    }
}
