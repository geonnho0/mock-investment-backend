package org.mockInvestment.stockPrice.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.mockInvestment.stockPrice.dto.*;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.mockInvestment.stockPrice.util.PeriodExtractor;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockPriceCandleFindService {

    private final StockTickerRepository stockTickerRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;


    public StockPriceCandlesResponse findStockPriceCandles(String stockCode, PeriodExtractor periodExtractor) {
        StockTicker stockTicker = stockTickerRepository.findByCode(stockCode)
                .orElseThrow(StockTickerNotFoundException::new);
        List<StockPriceCandle> priceCandles = stockPriceCandleRepository
                .findAllByStockTickerAndDateBetween(stockTicker, periodExtractor.getStart(), periodExtractor.getEnd());
        List<StockPriceCandleResponse> responses = priceCandles.stream()
                .map(StockPriceCandleResponse::new)
                .toList();
        return new StockPriceCandlesResponse(stockCode, responses);
    }

}
