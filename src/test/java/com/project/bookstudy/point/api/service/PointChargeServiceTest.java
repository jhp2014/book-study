package com.project.bookstudy.point.api.service;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.point.domain.PointCharge;
import com.project.bookstudy.point.domain.PointChargeStatus;
import com.project.bookstudy.point.repository.PointChargeRepository;
import com.project.bookstudy.point.service.PointChargeService;
import com.project.bookstudy.point.service.PointClient;
import com.project.bookstudy.point.service.dto.PointChargeDto;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import com.project.bookstudy.point.service.dto.PointSystemApprovalResponse;
import com.project.bookstudy.point.service.dto.PointSystemPreparationResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PointChargeServiceTest {

    @Autowired
    private PointChargeService pointChargeService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PointChargeRepository pointChargeRepository;
    @MockBean
    private PointClient pointClient;
    @Test
    void kakaoPointChargePrepareSuccess() {
        //give
        Long chargeAmount = 50_000L;

        String transactionId = "test_id";
        Map<String, String> extraData = Map.of("redirectUrl", "test_url");

        Member member = Member.builder()
                .email("email")
                .name("name")
                .build();
        memberRepository.save(member);

        given(pointClient.preparePointCharge(any(), any(), any()))
                .willReturn(PointSystemPreparationResponse.builder()
                        .transactionId(transactionId)
                        .extraData(extraData)
                        .build());

        //when
        PointChargePrepareServiceResponse pointChargePrepareServiceResponse = pointChargeService.prepare(member.getId(), chargeAmount);

        //then
        PointCharge pointCharge = pointChargeRepository.findById(pointChargePrepareServiceResponse.getPointChargeId()).orElseThrow();

        assertSoftly( softly -> {
            softly.assertThat(pointChargePrepareServiceResponse.getExtraData())
                    .extracting("redirectUrl")
                    .isEqualTo("test_url");

            softly.assertThat(pointCharge.getTransactionId()).isEqualTo(transactionId);
            softly.assertThat(pointCharge.getStatus()).isEqualTo(PointChargeStatus.IN_PROGRESS);
        });

    }

    @Test
    void kakaoPointChargeApprovalSuccess() {
        //give
        Long chargeAmount = 50_000L;
        String transactionId = "test_id";
        Map<String, String> extraData = Map.of("pgToken", "token value");
        LocalDateTime approvedDate = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
        String tempKey = UUID.randomUUID().toString();

        Member member = Member.builder()
                .email("email")
                .name("name")
                .build();
        memberRepository.save(member);

        PointCharge pointCharge = PointCharge.builder()
                .tempKey(tempKey)
                .transactionId(transactionId)
                .chargeAmount(chargeAmount)
                .member(member)
                .build();
        pointChargeRepository.save(pointCharge);


        given(pointClient.approvePointCharge(any(), any(), any()))
                .willReturn(PointSystemApprovalResponse.builder()
                        .totalAmount(chargeAmount)
                        .transactionId(transactionId)
                        .approvedAt(approvedDate)
                        .build());

        //when
        PointChargeDto pointChargeDto = pointChargeService.approve(tempKey, extraData);

        //then

        PointCharge findPointCharge = pointChargeRepository.findByIdFetchMember(pointChargeDto.getId()).orElseThrow();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(pointChargeDto.getTransactionId()).isEqualTo(transactionId);
            softAssertions.assertThat(pointChargeDto.getChargeDt()).isEqualTo(approvedDate);

            softAssertions.assertThat(findPointCharge.getChargeDt()).isEqualTo(approvedDate);
            softAssertions.assertThat(findPointCharge.getStatus()).isEqualTo(PointChargeStatus.SUCCESS);
        });
    }
}