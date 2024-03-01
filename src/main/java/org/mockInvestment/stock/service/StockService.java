package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.mockInvestment.stock.dto.StockCurrentPriceResponse;
import org.mockInvestment.stock.repository.StockPriceHistoryRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    private final StockPriceHistoryRepository stockPriceHistoryRepository;


    public StockService(StockRepository stockRepository, StockPriceHistoryRepository stockPriceHistoryRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceHistoryRepository = stockPriceHistoryRepository;
    }

    public StockCurrentPriceResponse findStockCurrentPrice(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        StockPriceHistory priceHistory = stockPriceHistoryRepository.findStockCurrentPrice(stock);

        return new StockCurrentPriceResponse(priceHistory.getPrice().getCurr());
    }
}
