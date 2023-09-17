package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.api.controller.request.StudyGroupCreateRequest;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StudyGroupRepositoryTest {

    @Autowired
    private StudyGroupRepository studyGroupRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Clock clock;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @DisplayName("조회 - 스터디 그룹 Fetch 리더 테스트")
    @Test
    void findStudyGroupWithMember() {
        //given
        Member member = createMember("email", "name");
        memberRepository.save(member);

        StudyGroupCreateRequest request = createStudyGroup(member.getId(), "subject", "content");
        StudyGroup studyGroup = request.toCreateServiceParam()
                .toEntityWithLeader(member);
        studyGroupRepository.save(studyGroup);

        //when
        StudyGroup findStudyGroup = studyGroupRepository.findByIdFetchLeader(studyGroup.getId())
                .orElseThrow(() -> new IllegalArgumentException("엔티티 없음"));



        //then
        assertThat(entityManagerFactory.getPersistenceUnitUtil().isLoaded(studyGroup.getLeader())).isTrue();
    }

    @DisplayName("조회 - 스터디 그룹 Fetch 리더 - 페이징 처리")
    @Test
    void searchStudyGroups() {
        //given
        int pageSize = 5;

        int totalDataNum = 20;
        IntStream.range(0, totalDataNum).forEach((i) -> {
            Member member = createMember("email" + i, "name_test" + i);
            memberRepository.save(member);

            StudyGroupCreateRequest request = createStudyGroup(member.getId(), "subject" + i, "contents" + i);
            StudyGroup savedGroup = studyGroupRepository.save(request.toCreateServiceParam().toEntityWithLeader(member));
        });

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        StudyGroupSearchCond emptyCond = StudyGroupSearchCond.builder()
                .leaderName(null)
                .subject(null)
                .build();

        //when
        Page<StudyGroup> studyGroups = studyGroupRepository.searchStudyGroupFetchLeader(pageRequest, emptyCond);

        //then
        assertThat(studyGroups.getTotalElements()).isEqualTo(totalDataNum);
        assertThat(studyGroups.getTotalPages()).isEqualTo(totalDataNum % pageSize == 0 ? totalDataNum / pageSize: (totalDataNum / pageSize) + 1);
        assertThat(studyGroups.getContent()).hasSize(pageSize);
        assertThat(studyGroups.getContent()).extracting("subject", "contents")
                .contains(
                        Tuple.tuple("subject0", "contents0"),
                        Tuple.tuple("subject1", "contents1"),
                        Tuple.tuple("subject2", "contents2"),
                        Tuple.tuple("subject3", "contents3"),
                        Tuple.tuple("subject4", "contents4")
                );

    }

    @DisplayName("조회 - 스터디 그룹 조회 시 스터디 제목으로 검색할 수 있다.")
    @Test
    void searchStudyGroupsWithSubject() {
        //given
        int pageSize = 5;

        Member member1 = createMember("email", "Kim");
        Member member2 = createMember("email", "Park");
        memberRepository.saveAll(List.of(member1, member2));

        StudyGroup studyGroup1 = createStudyGroup(member1.getId(), "일반 스터디1", "일반 내용").toCreateServiceParam().toEntityWithLeader(member1);
        StudyGroup studyGroup2 = createStudyGroup(member1.getId(), "일반 스터디2", "일반 내용").toCreateServiceParam().toEntityWithLeader(member1);
        StudyGroup studyGroup3 = createStudyGroup(member2.getId(), "일반 스터디3", "일반 내용").toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup4 = createStudyGroup(member2.getId(), "비밀 스터디1", "비밀 내용").toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup5 = createStudyGroup(member2.getId(), "특별 스터디1", "특별 내용").toCreateServiceParam().toEntityWithLeader(member2);

        List<StudyGroup> studyGroups = studyGroupRepository.saveAll(List.of(studyGroup1, studyGroup2, studyGroup3, studyGroup4, studyGroup5));

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        StudyGroupSearchCond subjectCond = StudyGroupSearchCond.builder()
                .leaderName(null)
                .subject("일반")
                .build();


        //when
        Page<StudyGroup> searchedStudyGroup
                = studyGroupRepository.searchStudyGroupFetchLeader(pageRequest, subjectCond);
        //then
        assertThat(searchedStudyGroup.getContent()).hasSize(3);
        assertThat(searchedStudyGroup.getContent())
                .extracting("subject")
                .contains(
                        "일반 스터디1", "일반 스터디2", "일반 스터디3"
                );
    }

    @DisplayName("조회 - 스터디 그룹 조회 시 스터디 리더 이름으로 검색할 수 있다.")
    @Test
    void searchStudyGroupsWithLeaderName() {
        //given
        int pageSize = 5;

        Member member1 = createMember("email", "Kim");
        Member member2 = createMember("email", "Park");
        memberRepository.saveAll(List.of(member1, member2));

        StudyGroup studyGroup1 = createStudyGroup(member1.getId(), "일반 스터디1", "일반 내용").toCreateServiceParam().toEntityWithLeader(member1);
        StudyGroup studyGroup2 = createStudyGroup(member1.getId(), "일반 스터디2", "일반 내용").toCreateServiceParam().toEntityWithLeader(member1);
        StudyGroup studyGroup3 = createStudyGroup(member2.getId(), "일반 스터디3", "일반 내용").toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup4 = createStudyGroup(member2.getId(), "비밀 스터디1", "비밀 내용").toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup5 = createStudyGroup(member2.getId(), "특별 스터디1", "특별 내용").toCreateServiceParam().toEntityWithLeader(member2);

        List<StudyGroup> studyGroups = studyGroupRepository.saveAll(List.of(studyGroup1, studyGroup2, studyGroup3, studyGroup4, studyGroup5));

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        StudyGroupSearchCond subjectCond = StudyGroupSearchCond.builder()
                .leaderName("Park")
                .subject(null)
                .build();

        //when
        Page<StudyGroup> searchedStudyGroup
                = studyGroupRepository.searchStudyGroupFetchLeader(pageRequest, subjectCond);
        //then
        assertThat(searchedStudyGroup.getContent()).hasSize(3);
        assertThat(searchedStudyGroup.getContent())
                .extracting("subject")
                .contains(
                        "일반 스터디3",
                        "비밀 스터디1",
                        "특별 스터디1"
                );
    }

    @DisplayName("조회 - 스터디 그룹 조회 시 스터디 리더 이름과 스터디 제목 으로 검색할 수 있다.")
    @Test
    void searchStudyGroupsWithLeaderNameAndSubject() {
        //given
        int pageSize = 5;

        Member member1 = createMember("email", "Kim");
        Member member2 = createMember("email", "Park");
        memberRepository.saveAll(List.of(member1, member2));

        StudyGroup studyGroup1 = createStudyGroup(member1.getId(), "일반 스터디1", "일반 내용").toCreateServiceParam().toEntityWithLeader(member1);
        StudyGroup studyGroup2 = createStudyGroup(member1.getId(), "일반 스터디2", "일반 내용").toCreateServiceParam().toEntityWithLeader(member1);
        StudyGroup studyGroup3 = createStudyGroup(member2.getId(), "일반 스터디3", "일반 내용").toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup4 = createStudyGroup(member2.getId(), "비밀 스터디1", "비밀 내용").toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup5 = createStudyGroup(member2.getId(), "특별 스터디1", "특별 내용").toCreateServiceParam().toEntityWithLeader(member2);

        List<StudyGroup> studyGroups = studyGroupRepository.saveAll(List.of(studyGroup1, studyGroup2, studyGroup3, studyGroup4, studyGroup5));

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        StudyGroupSearchCond subjectCond = StudyGroupSearchCond.builder()
                .leaderName("Park")
                .subject("일반")
                .build();

        //when
        Page<StudyGroup> searchedStudyGroup
                = studyGroupRepository.searchStudyGroupFetchLeader(pageRequest, subjectCond);
        //then
        assertThat(searchedStudyGroup.getContent()).hasSize(1);
        assertThat(searchedStudyGroup.getContent())
                .extracting("subject")
                .containsExactlyInAnyOrder(
                        "일반 스터디3"
                );
    }


    @DisplayName("스터디 그룹 조회 시 enrollment를 함께 조회할 수 있다.")
    @Test
    @Transactional
    void enrollSuccess() {
        Member leader = Member.builder()
                .name("leader")
                .email("email1")
                .build();
        Member member = Member.builder()
                .name("member")
                .email("email2")
                .build();
        member.chargePoint(50_000L);
        memberRepository.saveAll(List.of(leader, member));

        StudyGroup studyGroup = StudyGroup.builder()
                .subject("subject")
                .contents("contents")
                .contentsDetail("contentsDetail")
                .maxSize(3)
                .price(10_000L)
                .leader(leader)
                .build();
        studyGroupRepository.save(studyGroup);

//        Enrollment enrollment = Enrollment.of(member, studyGroup, LocalDateTime.of(2023, 3, 3, 3, 1));
//        enrollmentRepository.save(enrollment);

        //when
        StudyGroup findStudyGroup = studyGroupRepository.findByIdFetchEnrollment(studyGroup.getId()).
                orElseThrow(() -> new IllegalArgumentException("왜 안돼?"));

        entityManager.flush();

        //then

    }






    private StudyGroupCreateRequest createStudyGroup(long memberId, String subject, String contents) {
        return StudyGroupCreateRequest.builder()
                .memberId(memberId)
                .subject(subject)
                .contents(contents)
                .contentsDetail("contentsDetail")
                .maxSize(10)
                .price(25000L)
                .studyStartAt(LocalDateTime.of(2023, 1, 1, 0, 0, 2))
                .studyEndAt(LocalDateTime.of(2023, 1, 1, 0, 0, 3))
                .recruitmentStartAt(LocalDateTime.of(2023, 1, 1, 0, 0, 1))
                .recruitmentEndAt(LocalDateTime.of(2023, 1, 1, 0, 0, 2))
                .build();
    }


    private static Member createMember(String email, String name) {
        return Member.builder()
                .email(email)
                .name(name)
                .phone("phone")
                .career("career")
                .build();
    }
}