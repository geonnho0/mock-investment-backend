package org.mockInvestment.backTest.dto.response;

import java.time.LocalDate;

public record BackTestTradeHistory(
        boolean buy,
        double price,
        double amount,
        Double rsi,
        String cross,
        LocalDate tradeDate
) {

    public static BackTestTradeHistory of(boolean buy, double price, double amount, double rsi, LocalDate tradeDate) {
        return new BackTestTradeHistory(buy, price, amount, rsi, null, tradeDate);
    }

    public static BackTestTradeHistory of(boolean buy, double price, double amount, String cross, LocalDate tradeDate) {
        return new BackTestTradeHistory(buy, price, amount, null, cross, tradeDate);
    }

}
