package org.mockInvestment.backTest.dto.response;

import java.util.List;

public record BackTestResultResponse(List<BackTestTradeHistory> histories) {
}
