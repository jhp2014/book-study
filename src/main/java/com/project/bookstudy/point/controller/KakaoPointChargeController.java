package com.project.bookstudy.point.controller;

import com.project.bookstudy.common.api.ApiResonse;
import com.project.bookstudy.point.controller.dto.PointChargePrepareRequest;
import com.project.bookstudy.point.controller.dto.KakaoPointChargePrepareResponse;
import com.project.bookstudy.point.infra.KakaoPointClient;
import com.project.bookstudy.point.service.PointChargeService;
import com.project.bookstudy.point.service.dto.PointChargeDto;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class KakaoPointChargeController {


    private final PointChargeService pointChargeService;

    @PostMapping("/api/v1/kakao/point/ready")
    public ApiResonse<KakaoPointChargePrepareResponse> getPointChargeUrl(@RequestBody PointChargePrepareRequest request) {

        PointChargePrepareServiceResponse response = pointChargeService.prepare(request.getMemberId(), request.getPointAmount());
        KakaoPointChargePrepareResponse kakaoPointChargePrepareResponse = KakaoPointChargePrepareResponse.fromServiceResponse(response);

        return ApiResonse.ok(kakaoPointChargePrepareResponse);
    }

    @RequestMapping("/api/v1/kakao/point/approval/success")
    public ApiResonse<PointChargeDto> approvePointCharge(@RequestParam("temp_key") String tempKey, @RequestParam("pg_token") String token) {
        Map<String, String> extraRequestData = Map.of(KakaoPointClient.PG_TOKEN_KEY, token);
        PointChargeDto pointChargeDto = pointChargeService.approve(tempKey, extraRequestData);

        return ApiResonse.ok(pointChargeDto);
    }
}
