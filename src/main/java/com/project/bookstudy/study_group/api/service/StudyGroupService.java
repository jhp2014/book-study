package com.project.bookstudy.study_group.api.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.api.service.request.StudyGroupCreateServiceRequest;
import com.project.bookstudy.study_group.api.service.request.StudyGroupUpdateServiceRequest;
import com.project.bookstudy.study_group.api.service.response.StudyGroupCreateResponse;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.api.service.response.StudyGroupGetResponse;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupService {

    private final MemberRepository memberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final Clock clock;

    @Transactional
    public StudyGroupCreateResponse createStudyGroup(StudyGroupCreateServiceRequest request) {

        //스터디 날짜 예외 처리
        request.validateDateOrder(LocalDateTime.now(clock));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        StudyGroup savedStudyGroup = studyGroupRepository.save(request.toEntityWithLeader(member));
        return StudyGroupCreateResponse.from(savedStudyGroup);
    }

    public StudyGroupGetResponse getStudyGroup(Long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findByIdFetchLeader(studyGroupId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        return StudyGroupGetResponse.fromEntity(studyGroup);
    }

    public Page<StudyGroupGetResponse> getStudyGroupList(Pageable pageable, StudyGroupSearchCond cond) {
        Page<StudyGroup> studyGroups = studyGroupRepository.searchStudyGroupFetchLeader(pageable, cond);
        return studyGroups.map(StudyGroupGetResponse::fromEntity);
    }

    @Transactional
    public void updateStudyGroup(StudyGroupUpdateServiceRequest request) {
        //날짜 간 순서 검증
        request.validateDateOrder();

        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        studyGroup.update(request.toEntity());
    }

    @Transactional
    public void cancelStudyGroup(Long id) {
        StudyGroup studyGroup = studyGroupRepository.findByIdWithEnrollmentWithAll(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        if (studyGroup.isStarted()) {
            throw new IllegalStateException(ErrorMessage.STUDY_CANCEL_FAIL.getMessage());
        }

        studyGroup.cancel();
    }
}
