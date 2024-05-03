package org.mockInvestment.backTest.dto.request;

import java.time.LocalDate;

public record StrategyRequest(
        String stockCode,
        LocalDate startDate,
        LocalDate endDate,
        double buyRSI,
        double sellRSI,
        double amount
) {
}
