package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.LastStockInfoCacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockInfoService {

    private final LastStockInfoCacheRepository lastStockInfoCacheRepository;


    public StockInfoService(LastStockInfoCacheRepository lastStockInfoCacheRepository) {
        this.lastStockInfoCacheRepository = lastStockInfoCacheRepository;
    }

    public StockInfoResponse findStockInfo(String stockCode) {
        LastStockInfo stockInfo = lastStockInfoCacheRepository.findByStockCode(stockCode)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        double base = stockInfo.base();
        double currentPrice = stockInfo.curr();
        return new StockInfoResponse(stockInfo.name(), stockInfo.symbol(), base, currentPrice);
    }

}
