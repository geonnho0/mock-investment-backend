package org.mockInvestment.backTest.dto.response;

import java.time.LocalDate;

public record BackTestTradeHistory(
        boolean buy,
        double price,
        double amount,
        String message,
        LocalDate tradeDate
) {
}
