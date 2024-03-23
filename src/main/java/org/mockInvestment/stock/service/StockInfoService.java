package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.RecentStockInfo;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.RecentStockInfoCacheRepository;
import org.mockInvestment.stock.repository.StockPriceCandleRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockInfoService {

    private final StockRepository stockRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final RecentStockInfoCacheRepository recentStockInfoCacheRepository;


    public StockInfoService(StockRepository stockRepository, StockPriceCandleRepository stockPriceCandleRepository, RecentStockInfoCacheRepository recentStockInfoCacheRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceCandleRepository = stockPriceCandleRepository;
        this.recentStockInfoCacheRepository = recentStockInfoCacheRepository;
    }

    public StockInfoResponse findStockInfo(String stockCode) {
        RecentStockInfo stockInfo = recentStockInfoCacheRepository.findByStockCode(stockCode)
                .orElseGet(() -> {
                    RecentStockInfo findStockInfo = findRecentStockInfo(stockCode);
                    recentStockInfoCacheRepository.saveByCode(stockCode, findStockInfo);
                    return findStockInfo;
                });
        double base = stockInfo.base();
        double currentPrice = stockInfo.curr();
        return new StockInfoResponse(stockInfo.name(), stockInfo.symbol(), base, currentPrice);
    }

    private RecentStockInfo findRecentStockInfo(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceCandle> stockPriceCandles = stockPriceCandleRepository.findTop2ByStockOrderByDateDesc(stock);
        StockPriceCandle recentCandle = stockPriceCandles.get(0);
        return new RecentStockInfo(stockPriceCandles.get(1).getClose(), stock, recentCandle);
    }

}
