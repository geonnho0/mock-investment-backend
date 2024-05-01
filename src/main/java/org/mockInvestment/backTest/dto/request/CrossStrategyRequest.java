package org.mockInvestment.backTest.dto.request;

import java.time.LocalDate;

public record CrossStrategyRequest(
        boolean macd,
        boolean movingAverage,
        String stockCode,
        LocalDate startDate,
        LocalDate endDate,
        double amount
) {
}
