package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.StockPriceHistoryRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    private final StockPriceHistoryRepository stockPriceHistoryRepository;


    public StockService(StockRepository stockRepository, StockPriceHistoryRepository stockPriceHistoryRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceHistoryRepository = stockPriceHistoryRepository;
    }

    public StockInfoDetailResponse findStockInfoDetail(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceHistory> priceHistories = stockPriceHistoryRepository.findTop2ByStockOrderByDateDesc(stock);
        double currentPrice = priceHistories.get(0).getPrice().getCurr();
        double base = priceHistories.get(1).getPrice().getClose();
        return new StockInfoDetailResponse(stock, base, currentPrice);
    }

    public StockInfoSummariesResponse findStockInfoSummaries(List<String> stockCodes) {
        List<StockInfoSummaryResponse> prices = new ArrayList<>();
        for (String code : stockCodes) {
            Stock stock = stockRepository.findByCode(code)
                    .orElseThrow(StockNotFoundException::new);
            List<StockPriceHistory> priceHistories = stockPriceHistoryRepository.findTop2ByStockOrderByDateDesc(stock);
            double currentPrice = priceHistories.get(0).getPrice().getCurr();
            double base = priceHistories.get(1).getPrice().getClose();
            prices.add(new StockInfoSummaryResponse(code, stock.getName(), base, currentPrice));
        }
        return new StockInfoSummariesResponse(prices);
    }

    public StockPriceCandlesResponse findStockPriceHistories(String stockCode, PeriodExtractor periodExtractor) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceHistory> priceHistories = stockPriceHistoryRepository
                .findAllByStockAndDateBetween(stock, periodExtractor.getStart(), periodExtractor.getEnd());
        List<StockPriceCandleResponse> responses = priceHistories.stream()
                .map(StockPriceCandleResponse::new)
                .toList();
        return new StockPriceCandlesResponse(stockCode, responses);
    }

}
