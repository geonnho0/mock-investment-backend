package org.mockInvestment.backTest.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.backTest.dto.request.CrossStrategyRequest;
import org.mockInvestment.backTest.dto.response.BackTestResultResponse;
import org.mockInvestment.backTest.dto.response.BackTestTradeHistory;
import org.mockInvestment.stockPrice.application.StockPriceCandleFindService;
import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.mockInvestment.stockPrice.util.PeriodExtractor;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackTestCrossStrategyService {

    private final StockTickerRepository stockTickerRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final StockPriceCandleFindService stockPriceCandleFindService;


    public BackTestResultResponse runTest(CrossStrategyRequest request) {
        StockTicker stockTicker = stockTickerRepository.findByCode(request.stockCode())
                .orElseThrow(StockTickerNotFoundException::new);
        List<BackTestTradeHistory> histories = new ArrayList<>();
        LocalDate startDate = request.startDate();
        while (startDate.isBefore(request.endDate())) {
            PeriodExtractor periodExtractor = new PeriodExtractor(startDate, "6m");
            List<StockPriceCandle> stockPriceCandles = stockPriceCandleFindService.findStockPriceCandles(stockTicker, periodExtractor);

            double currentPrice = stockPriceCandles.get(stockPriceCandles.size() - 1).getClose();

            if (histories.isEmpty()) {
                histories.add(BackTestTradeHistory.of(true, currentPrice, request.amount(), null, startDate));
                startDate = getNextMarketDate(startDate);
                continue;
            }

            double[] macd = TechnicalIndicatorCalculator.calculateMACD(stockPriceCandles);
            double[] signalLine = TechnicalIndicatorCalculator.calculateSignalLine(macd);

            BackTestTradeHistory lastOrder = histories.get(histories.size() - 1);
            double amount = calcAmount(lastOrder.amount(), lastOrder.price(), currentPrice);

            double currentMACD = macd[macd.length - 2];
            double currentSignalLine = signalLine[signalLine.length - 2];
            double previousMACD = macd[macd.length - 3];
            double previousSignalLine = signalLine[signalLine.length - 3];

            if (currentMACD > currentSignalLine && previousMACD < previousSignalLine) {
                if (!lastOrder.buy())
                    histories.add(BackTestTradeHistory.of(true, currentPrice, amount, "Golden", startDate));
            } else if (currentMACD < currentSignalLine && previousMACD > previousSignalLine) {
                if (lastOrder.buy())
                    histories.add(BackTestTradeHistory.of(false, currentPrice, amount, "Dead", startDate));
            }
            startDate = getNextMarketDate(startDate);
        }
        return new BackTestResultResponse(histories);
    }

    private double calcAmount(double previousAmount, double previousPrice, double currentPrice) {
        return previousAmount * (currentPrice / previousPrice);
    }

    private LocalDate getNextMarketDate(LocalDate previousDate) {
        List<LocalDate> dates = stockPriceCandleRepository.findCandidateDates(previousDate, PageRequest.of(0, 2));
        return dates.get(1);
    }
}
