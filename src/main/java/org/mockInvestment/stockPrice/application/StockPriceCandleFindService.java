package org.mockInvestment.stockPrice.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.mockInvestment.stockPrice.dto.*;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.mockInvestment.stockPrice.util.PeriodExtractor;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockPriceCandleFindService {

    private final StockTickerRepository stockTickerRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;


    public StockPriceCandlesResponse findStockPriceCandles(String stockCode, PeriodExtractor periodExtractor) {
        StockTicker stockTicker = stockTickerRepository.findTop1ByCodeAndDateLessThanEqualOrderByDateDesc(stockCode, periodExtractor.getEnd()).get(0);
        List<StockPriceCandle> priceCandles = stockPriceCandleRepository
                .findAllByStockTickerAndDateBetween(stockTicker.getCode(), periodExtractor.getStart(), periodExtractor.getEnd());
        List<StockPriceCandleResponse> responses = priceCandles.stream()
                .map(StockPriceCandleResponse::new)
                .toList();
        return new StockPriceCandlesResponse(stockCode, responses);
    }

}
