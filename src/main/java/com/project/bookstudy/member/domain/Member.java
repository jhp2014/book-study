package com.project.bookstudy.member.domain;

import com.project.bookstudy.common.exception.ErrorMessage;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;
    @Column(nullable = false)
    private String email;
    private String name;
    private String phone;
    @Lob
    private String career;
    private Long point = 0L; //나중에 Point class 따로 생성?
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    private Member(String email, String name, String phone, String career) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.career = career;

        this.point = 0L;
        this.status = MemberStatus.ACTIVE;
        this.role = Role.MEMBER;
    }

    public void updateName(String name) {
        if (name == null) return;
        this.name = name;
    }

    public void chargePoint(Long point) {
        this.point += point;
    }

    public void usePoint(Long point) {
        if (this.point < point) throw new IllegalStateException(ErrorMessage.NOT_ENOUGH_POINT.getMessage());
        this.point -= point;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Member member = (Member) o;
        return getId() != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
