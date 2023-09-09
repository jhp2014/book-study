package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.member.domain.QMember;
import com.project.bookstudy.study_group.domain.QPayment;
import com.project.bookstudy.study_group.domain.QStudyGroup;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.project.bookstudy.member.domain.QMember.member;
import static com.project.bookstudy.study_group.domain.QEnrollment.enrollment;
import static com.project.bookstudy.study_group.domain.QPayment.payment;
import static com.project.bookstudy.study_group.domain.QStudyGroup.studyGroup;

@RequiredArgsConstructor
public class CustomStudyGroupRepositoryImpl implements CustomStudyGroupRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<StudyGroup> findByIdWithEnrollments(Long id) {

        StudyGroup studyGroup = jpaQueryFactory
                .selectFrom(QStudyGroup.studyGroup)
                .leftJoin(QStudyGroup.studyGroup.enrollments, enrollment).fetchJoin()
                .fetchOne();

        return Optional.ofNullable(studyGroup);
    }

    @Override
    public Optional<StudyGroup> findByIdWithEnrollmentWithAll(Long id) {

        StudyGroup studyGroup = jpaQueryFactory
                .selectFrom(QStudyGroup.studyGroup)
                .leftJoin(QStudyGroup.studyGroup.enrollments, enrollment).fetchJoin()
                .join(enrollment.member, member).fetchJoin()
                .join(enrollment.payment, payment).fetchJoin()
                .fetchOne();

        return Optional.ofNullable(studyGroup);
    }

    @Override
    public Optional<StudyGroup> findByIdWithLeader(Long id) {
        StudyGroup studyGroup = jpaQueryFactory
                .selectFrom(QStudyGroup.studyGroup)
                .where(QStudyGroup.studyGroup.id.eq(id))
                .join(QStudyGroup.studyGroup.leader, member).fetchJoin()
                .fetchOne();

        return Optional.ofNullable(studyGroup);
    }

    @Override
    public Page<StudyGroup> searchStudyGroupWithLeader(Pageable pageable, StudyGroupSearchCond cond) {

        List<StudyGroup> studyGroups = jpaQueryFactory
                .selectFrom(studyGroup)
                .join(studyGroup.leader, member).fetchJoin()
                .where(leaderNameCond(cond.getLeaderName()), subjectCond(cond.getSubject()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(studyGroup.count())
                .from(studyGroup)
                .fetchOne();

        return new PageImpl<StudyGroup>(studyGroups, pageable, totalCount);
    }

    private BooleanExpression leaderNameCond(String leaderName) {
        if (!StringUtils.hasText(leaderName)) return null;
        return member.name.contains(leaderName);
    }

    private BooleanExpression subjectCond(String subject) {
        if (!StringUtils.hasText(subject)) return null;
        return studyGroup.subject.contains(subject);
    }
}
