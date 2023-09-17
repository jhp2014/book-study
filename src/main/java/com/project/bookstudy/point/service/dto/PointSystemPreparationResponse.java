package com.project.bookstudy.point.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class PointSystemPreparationResponse {

    private String transactionId;
    private Map<String, String> extraData;

    @Builder
    public PointSystemPreparationResponse(String transactionId, Map<String, String> extraData) {
        this.transactionId = transactionId;
        this.extraData = extraData;
    }
}
