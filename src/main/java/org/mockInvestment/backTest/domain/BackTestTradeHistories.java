package org.mockInvestment.backTest.domain;

import org.mockInvestment.backTest.dto.response.BackTestTradeHistory;

import java.util.ArrayList;
import java.util.List;

public class BackTestTradeHistories {

    private List<BackTestTradeHistory> histories = new ArrayList<>();


    public boolean isEmpty() {
        return histories.isEmpty();
    }

    public void addHistoryIfEmpty(BackTestTradeHistory history) {
        if (histories.isEmpty()) {
            histories.add(history);
        }
    }

    public void addHistoryIfLastTradeIsBuy(BackTestTradeHistory history) {
        if (isLastTradeBuy()) {
            histories.add(history);
        }
    }

    public void addHistoryIfLastTradeIsSell(BackTestTradeHistory history) {
        if (!isLastTradeBuy()) {
            histories.add(history);
        }
    }

    public BackTestTradeHistory getLastTrade() {
        if (histories.isEmpty()) {
            throw new IllegalStateException("No histories have been added to the result");
        }
        return histories.get(histories.size() - 1);
    }

    public List<BackTestTradeHistory> getHistories() {
        return histories;
    }

    public double calculateLastAmount(double currentPrice) {
        double previousAmount = getLastTrade().amount();
        double previousPrice = getLastTrade().price();
        return previousAmount * (currentPrice / previousPrice);
    }

    private boolean isLastTradeBuy() {
        return getLastTrade().buy();
    }

}
