package org.mockInvestment.backTest.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.backTest.domain.BackTestTradeHistories;
import org.mockInvestment.backTest.dto.request.StrategyRequest;
import org.mockInvestment.backTest.dto.response.BackTestResultResponse;
import org.mockInvestment.backTest.dto.response.BackTestTradeHistory;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackTestRSIStrategyService {

    private final StockPriceFindService stockPriceFindService;


    public BackTestResultResponse runTest(StrategyRequest request) {
        List<StockPriceCandle> prices = stockPriceFindService
                .findStockPricesBetweenDate(request.stockCode(), request.startDate(), request.endDate());
        RSICalculator calculator = new RSICalculator(prices.stream().map(StockPriceCandle::getClose).toList());
        BackTestTradeHistories histories = new BackTestTradeHistories();

        for (StockPriceCandle price : prices) {
            double rsi = calculator.calculate();
            addHistoryWhenNecessary(histories, price.getClose(), rsi, request, price.getDate());
            calculator.add(price.getClose());
        }
        return BackTestResultResponse.from(histories);
    }

    private void addHistoryWhenNecessary(
            BackTestTradeHistories histories,
            double currentPrice,
            double rsi,
            StrategyRequest request,
            LocalDate date
    ) {
        if (histories.isEmpty()) {
            String message = "Buy with rsi: " + rsi;
            histories.addHistoryIfEmpty(new BackTestTradeHistory(true, currentPrice, request.amount(), message, date));
            return;
        }
        double amount = histories.calculateLastAmount(currentPrice);
        if (rsi >= request.sellRSI()) {
            String message = "Sell with rsi: " + rsi;
            histories.addHistoryIfLastTradeIsBuy(new BackTestTradeHistory(false, currentPrice, amount, message, date));
            return;
        }
        if (rsi <= request.buyRSI()) {
            String message = "Buy with rsi: " + rsi;
            histories.addHistoryIfLastTradeIsSell(new BackTestTradeHistory(true, currentPrice, amount, message, date));
        }
    }

}
