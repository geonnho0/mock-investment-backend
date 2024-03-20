package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.LastStockInfoCacheRepository;
import org.mockInvestment.stock.repository.StockPriceCandleRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockPriceService {

    private final StockRepository stockRepository;

    private final LastStockInfoCacheRepository lastStockInfoCacheRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;


    public StockPriceService(StockRepository stockRepository, LastStockInfoCacheRepository lastStockInfoCacheRepository, StockPriceCandleRepository stockPriceCandleRepository) {
        this.stockRepository = stockRepository;
        this.lastStockInfoCacheRepository = lastStockInfoCacheRepository;
        this.stockPriceCandleRepository = stockPriceCandleRepository;
    }

    public StockPricesResponse findStockPrices(List<String> stockCodes) {
        List<StockPriceResponse> responses = new ArrayList<>();
        for (String code : stockCodes) {
            LastStockInfo stockInfo = lastStockInfoCacheRepository.findByStockCode(code)
                    .orElseThrow(JsonStringDeserializationFailureException::new);

            responses.add(StockPriceResponse.of(code, stockInfo));
        }
        return new StockPricesResponse(responses);
    }

    public StockPriceCandlesResponse findStockPriceCandles(String stockCode, PeriodExtractor periodExtractor) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceCandle> priceCandles = stockPriceCandleRepository
                .findAllByStockAndDateBetween(stock, periodExtractor.getStart(), periodExtractor.getEnd());
        List<StockPriceCandleResponse> responses = priceCandles.stream()
                .map(StockPriceCandleResponse::new)
                .toList();
        return new StockPriceCandlesResponse(stockCode, responses);
    }

}
