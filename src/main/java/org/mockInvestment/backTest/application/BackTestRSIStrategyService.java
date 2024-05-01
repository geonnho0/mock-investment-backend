package org.mockInvestment.backTest.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.backTest.dto.request.RSIStrategyRequest;
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
public class BackTestRSIStrategyService {

    private final StockTickerRepository stockTickerRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final StockPriceCandleFindService stockPriceCandleFindService;


    public BackTestResultResponse runTest(RSIStrategyRequest request) {
        StockTicker stockTicker = stockTickerRepository.findByCode(request.stockCode())
                .orElseThrow(StockTickerNotFoundException::new);
        List<BackTestTradeHistory> histories = new ArrayList<>();
        LocalDate startDate = request.startDate();
        while (startDate.isBefore(request.endDate())) {
            PeriodExtractor periodExtractor = new PeriodExtractor(startDate, "6m");
            List<StockPriceCandle> stockPriceCandles = stockPriceCandleFindService.findStockPriceCandles(stockTicker, periodExtractor);
            double rsi = TechnicalIndicatorCalculator.calculateRSI(stockPriceCandles);
            double currentPrice = stockPriceCandles.get(stockPriceCandles.size() - 1).getClose();

            if (histories.isEmpty()) {
                histories.add(BackTestTradeHistory.of(true, currentPrice, request.amount(), rsi, startDate));
                startDate = getNextMarketDate(startDate);
                continue;
            }

            BackTestTradeHistory lastOrder = histories.get(histories.size() - 1);
            double amount = calcAmount(lastOrder.amount(), lastOrder.price(), currentPrice);
            if (rsi >= request.sellRSI()) {  // should sell
                if (lastOrder.buy())
                    histories.add(BackTestTradeHistory.of(false, currentPrice, amount, rsi, startDate));
            }
            else if (rsi <= request.buyRSI()) {  // should buy
                if (!lastOrder.buy())
                    histories.add(BackTestTradeHistory.of(true, currentPrice, amount, rsi, startDate));
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
