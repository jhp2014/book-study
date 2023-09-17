package com.project.bookstudy.point.infra;

import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.point.service.PointClient;
import com.project.bookstudy.point.service.dto.PointChargeDto;
import com.project.bookstudy.point.service.dto.PointSystemApprovalResponse;
import com.project.bookstudy.point.service.dto.PointSystemPreparationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoPointClient implements PointClient {

    /**
     * 다른 곳에서 Map의 값을 꺼내올 때 사용하는 Key
     */
    public static final String REDIRECT_URL_KEY = "redirectUrl";
    public static final String PG_TOKEN_KEY = "pgToken";


    /**
     * Kakao에 요청시 필요한 값
     */
    private static final String FIXED_CID = "TC0ONETIME";
    private static final String FIXED_PARTNER_ORDER_ID = "SAME";
    private static final String FIXED_ITEM_NAME = "CHARGE_POINT";
    private static final String FIXED_QUANTITY = "1";
    private static final String FIXED_VAT_AMOUNT = "0";
    private static final String FIXED_TAX_FREE_AMOUNT = "0";
    private static final String APPROVAL_URL = "http://localhost:8080/api/v1/kakao/point/approval/success";
    private static final String CANCEL_URL = "http://localhost:8080/api/v1/kakao/point/approval/cancel";
    private static final String FAIL_URL = "http://localhost:8080/api/v1/kakao/point/approval/fail";
    private static final String KAKAO_BASE_URL = "https://kapi.kakao.com/v1/payment";
    private final String admin_Key = "ef47041343d62d8524d27d836e5f0947";


    private final RestTemplate restTemplate;

    @Override
    public PointSystemPreparationResponse preparePointCharge(MemberDto memberDto, Long pointAmount, String tempKey) {

        //prepare request param
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>() {{
            add("cid", FIXED_CID);
            put("partner_order_id", List.of(FIXED_PARTNER_ORDER_ID));
            put("partner_user_id", List.of(String.valueOf(memberDto.getId())));
            put("item_name", List.of(FIXED_ITEM_NAME));
            put("quantity", List.of(FIXED_QUANTITY));
            put("total_amount", List.of(String.valueOf(pointAmount)));
            put("vat_amount", List.of(FIXED_VAT_AMOUNT));
            put("tax_free_amount", List.of(FIXED_TAX_FREE_AMOUNT));
            put("approval_url", List.of(getApprovalUrlWithQueryString("temp_key", tempKey)));
            put("cancel_url", List.of(CANCEL_URL));
            put("fail_url", List.of(FAIL_URL));
        }};

        //prepare url
        URI paymentReadyUrl = getUriFrom("/ready");

        HttpEntity<MultiValueMap<String, String>> http = new HttpEntity<>(requestBody, createHeader());


        //send request
        KakaoReadyResponse readyResponse = restTemplate.postForObject(paymentReadyUrl, http, KakaoReadyResponse.class);

        return PointSystemPreparationResponse.builder()
                .transactionId(readyResponse.getTid())
                .extraData(Map.of(REDIRECT_URL_KEY, readyResponse.getNext_redirect_pc_url()))
                .build();
    }

    private URI getUriFrom(String path) {
        return UriComponentsBuilder
                .fromUriString(KAKAO_BASE_URL).path(path)
                .encode()
                .build()
                .toUri();
    }

    private HttpHeaders createHeader() {
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        requestHeader.set("Authorization", "KakaoAK " + admin_Key);
        return requestHeader;
    }

    private String getApprovalUrlWithQueryString(String param, String value) {
        /**
         * make APPROVAL_URL?param=value
         */
        StringBuilder queryString = new StringBuilder()
                .append(APPROVAL_URL).append("?").append(param).append("=").append(value);
        return queryString.toString();
    }

    @Override
    public PointSystemApprovalResponse approvePointCharge(MemberDto memberDto, PointChargeDto history, Map<String, String> extraRequestData) {

        //prepare param
        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>() {{
            add("cid", FIXED_CID);
            add("tid", history.getTransactionId());
            add("partner_order_id", FIXED_PARTNER_ORDER_ID);
            add("partner_user_id", String.valueOf(memberDto.getId()));
            add("pg_token", extraRequestData.get(PG_TOKEN_KEY));
        }};

        //prepare uri
        URI paymentApprovalUrl = getUriFrom("/approve");

        HttpEntity<MultiValueMap<String, String>> http = new HttpEntity<>(requestParam, createHeader());
        KakaoApproveResponse response = restTemplate.postForObject(paymentApprovalUrl, http, KakaoApproveResponse.class);
        ;

        return PointSystemApprovalResponse.builder()
                .totalAmount((long) response.getAmount().getTotal())
                .transactionId(response.getTid())
                .approvedAt(LocalDateTime.parse(response.getApproved_at(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .build();
    }

}
