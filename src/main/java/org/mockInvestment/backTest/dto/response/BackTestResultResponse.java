package org.mockInvestment.backTest.dto.response;

import org.mockInvestment.backTest.domain.BackTestTradeHistories;

import java.util.List;

public record BackTestResultResponse(List<BackTestTradeHistory> histories) {

    public static BackTestResultResponse from(BackTestTradeHistories entity) {
        return new BackTestResultResponse(entity.getHistories());
    }

}
