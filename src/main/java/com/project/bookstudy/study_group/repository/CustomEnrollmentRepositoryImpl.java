package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.Enrollment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.project.bookstudy.member.domain.QMember.member;
import static com.project.bookstudy.study_group.domain.QEnrollment.enrollment;
import static com.project.bookstudy.study_group.domain.QPayment.payment;

@RequiredArgsConstructor
public class CustomEnrollmentRepositoryImpl implements CustomEnrollmentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Enrollment> findByStudyGroupIdWithPaymentWithMember(Long id) {

         return jpaQueryFactory.selectFrom(enrollment)
                 .join(enrollment.payment, payment).fetchJoin()
                 .join(payment.member, member).fetchJoin()
                .where(enrollment.id.eq(id))
                .fetch();
    }
}
