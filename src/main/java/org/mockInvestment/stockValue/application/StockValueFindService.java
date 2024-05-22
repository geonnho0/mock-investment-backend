package org.mockInvestment.stockValue.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.dto.StockPriceResponse;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.stockValue.dto.StockValueRankingResponse;
import org.mockInvestment.stockValue.dto.StockValueResponse;
import org.mockInvestment.stockValue.dto.StockValuesRankingResponse;
import org.mockInvestment.stockValue.dto.StockValuesResponse;
import org.mockInvestment.stockValue.repository.StockValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockValueFindService {

    private final StockPriceFindService stockPriceFindService;

    private final StockValueRepository stockValueRepository;

    private final StockTickerRepository stockTickerRepository;


    public StockValuesResponse findStockValuesByCode(String stockCode, String date) {
        LocalDate newDate = LocalDate.parse(date);
        StockTicker stockTicker = stockTickerRepository.findByCode(stockCode)
                .orElseThrow(StockTickerNotFoundException::new);
        List<StockValueResponse> responses = stockValueRepository
                .findAllByStockTickerAndDateIsLessThanEqual(stockTicker, newDate)
                .stream()
                .map(StockValueResponse::of)
                .toList();

        return new StockValuesResponse(responses);
    }

}
