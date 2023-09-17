package com.project.bookstudy.point.service;

import com.project.bookstudy.common.exception.ErrorMessage;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.point.domain.PointCharge;
import com.project.bookstudy.point.repository.PointChargeRepository;
import com.project.bookstudy.point.service.dto.PointChargeDto;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import com.project.bookstudy.point.service.dto.PointSystemApprovalResponse;
import com.project.bookstudy.point.service.dto.PointSystemPreparationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointChargeService {

    private final PointClient pointClient;
    private final MemberRepository memberRepository;
    private final PointChargeRepository pointChargeRepository;

    public PointChargePrepareServiceResponse prepare(Long memberId, Long chargeAmount) {

        //setting
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        //logic
        String tempKey = UUID.randomUUID().toString();
        PointSystemPreparationResponse response = pointClient.preparePointCharge(MemberDto.fromEntity(member), chargeAmount, tempKey);

        PointCharge pointCharge = PointCharge.builder()
                .tempKey(tempKey)
                .transactionId(response.getTransactionId())
                .chargeAmount(chargeAmount)
                .member(member)
                .build();

        pointChargeRepository.save(pointCharge);

        return PointChargePrepareServiceResponse.builder()
                .pointChargeId(pointCharge.getId())
                .extraData(response.getExtraData())
                .build();
    }

    @Transactional
    public PointChargeDto approve(String tempKey, Map<String, String> extraRequestData) {

        //setting
        PointCharge pointCharge = pointChargeRepository.findByTempKey(tempKey)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NO_ENTITY.getMessage()));

        Member member = pointCharge.getMember();

        //logic
        PointSystemApprovalResponse response = pointClient.approvePointCharge(MemberDto.fromEntity(member), PointChargeDto.fromEntity(pointCharge), extraRequestData);
        validate(response, pointCharge);
        pointCharge.success(response.getApprovedAt());

        return PointChargeDto.fromEntity(pointCharge);
    }

    private void validate(PointSystemApprovalResponse response, PointCharge pointCharge) {
        if (!response.getTransactionId().equals(pointCharge.getTransactionId())
                || pointCharge.getChargeAmount().longValue() != response.getTotalAmount().longValue()) {
            pointCharge.fail();
            throw new IllegalStateException(ErrorMessage.CHARGE_FAIL.getMessage());
        }
    }
}
