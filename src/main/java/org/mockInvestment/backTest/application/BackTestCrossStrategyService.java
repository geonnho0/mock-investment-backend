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
public class BackTestCrossStrategyService {

    private final StockPriceFindService stockPriceFindService;


    public BackTestResultResponse runTest(StrategyRequest request) {
        List<StockPriceCandle> prices = stockPriceFindService
                .findStockPricesBetweenDate(request.stockCode(), request.startDate(), request.endDate());
        MACDCalculator calculator = new MACDCalculator(prices.stream().map(StockPriceCandle::getClose).toList());
        BackTestTradeHistories histories = new BackTestTradeHistories();

        for (StockPriceCandle price : prices) {
            double[] macd = calculator.calculateMACD();
            double[] signalLine = calculator.calculateSignalLine();
            addHistoryWhenNecessary(histories, price.getClose(), macd, signalLine, request, price.getDate());
            calculator.add(price.getClose());
        }
        return BackTestResultResponse.from(histories);
    }

    private void addHistoryWhenNecessary(
            BackTestTradeHistories histories,
            double currentPrice,
            double[] macd,
            double[] signalLine,
            StrategyRequest request,
            LocalDate date
    ) {
        if (histories.isEmpty()) {
            String message = "Back-test start!";
            histories.addHistoryIfEmpty(new BackTestTradeHistory(true, currentPrice, request.amount(), message, date));
            return;
        }
        double amount = histories.calculateLastAmount(currentPrice);
        double currentMACD = macd[macd.length - 2];
        double currentSignalLine = signalLine[signalLine.length - 2];
        double previousMACD = macd[macd.length - 3];
        double previousSignalLine = signalLine[signalLine.length - 3];
        if (currentMACD > currentSignalLine && previousMACD < previousSignalLine) {
            String message = "Buy with gold cross";
            histories.addHistoryIfLastTradeIsSell(new BackTestTradeHistory(true, currentPrice, amount, message, date));
            return;
        }
        if (currentMACD < currentSignalLine && previousMACD > previousSignalLine) {
            String message = "Sell with dead cross";
            histories.addHistoryIfLastTradeIsBuy(new BackTestTradeHistory(false, currentPrice, amount, message, date));
        }
    }

}
