package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupService {

    private final MemberRepository memberRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Transactional
    public StudyGroupDto createStudyGroup(Long memberId, CreateStudyGroupParam studyGroupParam) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        StudyGroup savedStudyGroup = studyGroupRepository.save(StudyGroup.from(member, studyGroupParam));
        StudyGroupDto studyGroupDto = StudyGroupDto.fromEntity(savedStudyGroup, member);
        return studyGroupDto;
    }

    public StudyGroupDto getStudyGroup(Long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findByIdWithLeader(studyGroupId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        StudyGroupDto studyGroupDto = StudyGroupDto.fromEntity(studyGroup, studyGroup.getLeader());
        return studyGroupDto;
    }

    public Page<StudyGroupDto> getStudyGroupList(Pageable pageable, StudyGroupSearchCond cond) {
        Page<StudyGroup> studyGroups = studyGroupRepository.searchStudyGroup(pageable, cond);
        return studyGroups.map(entity -> StudyGroupDto.fromEntity(entity, entity.getLeader()));
    }

    @Transactional
    public void updateStudyGroup(UpdateStudyGroupParam updateParam) {
        StudyGroup studyGroup = studyGroupRepository.findById(updateParam.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        studyGroup.update(updateParam);
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
