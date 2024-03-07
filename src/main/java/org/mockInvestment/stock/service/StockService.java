package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.StockPriceHistoryRepository;
import org.mockInvestment.stock.repository.StockRedisRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    private final StockPriceHistoryRepository stockPriceHistoryRepository;

    private final StockRedisRepository stockRedisRepository;

    public StockService(StockRepository stockRepository, StockPriceHistoryRepository stockPriceHistoryRepository, StockRedisRepository stockRedisRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceHistoryRepository = stockPriceHistoryRepository;
        this.stockRedisRepository = stockRedisRepository;
    }

    public StockInfoDetailResponse findStockInfoDetail(String stockCode) {
        Map<String, String> stockInfo = stockRedisRepository.get(stockCode);
        double base = getBase(stockCode, stockInfo);
        double currentPrice = Double.parseDouble(stockInfo.get("curr"));
        return new StockInfoDetailResponse(stockInfo.get("name"), stockInfo.get("symbol"), base, currentPrice);
    }

    public StockInfoSummariesResponse findStockInfoSummaries(List<String> stockCodes) {
        List<StockInfoSummaryResponse> prices = new ArrayList<>();
        for (String code : stockCodes) {
            Map<String, String> stockInfo = stockRedisRepository.get(code);
            String stockName = stockInfo.get("name");
            double base = getBase(code, stockInfo);
            double currentPrice = Double.parseDouble(stockInfo.get("curr"));

            prices.add(new StockInfoSummaryResponse(code, stockName, base, currentPrice));
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

    private Double getBase(String stockCode, Map<String, String> stockInfo) {
        if (stockInfo.get("base") != null) {
            return Double.parseDouble(stockInfo.get("base"));
        }

        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceHistory> priceHistories = stockPriceHistoryRepository.findTop2ByStockOrderByDateDesc(stock);
        double base = priceHistories.get(1).getPrice().getClose();
        stockRedisRepository.put(stockCode, "base", String.valueOf(base));
        return base;
    }

}
