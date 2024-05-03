package org.mockInvestment.backTest.dto.request;

import java.time.LocalDate;

public record CrossStrategyRequest(
        String stockCode,
        LocalDate startDate,
        LocalDate endDate,
        double amount
) {
}
