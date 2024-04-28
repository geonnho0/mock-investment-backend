package org.mockInvestment.stockValue.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.dto.StockPriceResponse;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.stockValue.domain.StockValue;
import org.mockInvestment.stockValue.dto.StockValueRankingResponse;
import org.mockInvestment.stockValue.dto.StockValueResponse;
import org.mockInvestment.stockValue.dto.StockValuesRankingResponse;
import org.mockInvestment.stockValue.dto.StockValuesResponse;
import org.mockInvestment.stockValue.repository.StockValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


//        Map<String, Map<LocalDate, Map<String, Double>>> map = new HashMap<>();
//        for (StockValue value : values) {
//            Map<LocalDate, Map<String, Double>> dateMappedData = map.getOrDefault(value.getCode(), new HashMap<>());
//            Map<String, Double> data = dateMappedData.getOrDefault(value.getDate(), new HashMap<>());
//            data.put(value.getIndicator(), value.getValue());
//            dateMappedData.put(value.getDate(), data);
//            map.put(value.getCode(), dateMappedData);
//        }
//
//        for (String code: map.keySet()) {
//            Map<LocalDate, Map<String, Double>> dateMappedData = map.get(code);
//            for (LocalDate pivotDate: dateMappedData.keySet()) {
//                responses.add(StockValueResponse.of(code, pivotDate, dateMappedData.get(pivotDate)));
//            }
//        }


        return new StockValuesResponse(responses);
    }

    public StockValuesRankingResponse findStockValuesRanking(String date) {
        List<StockValueResponse> values = findTop20StockValues(date).values();
        List<StockValueRankingResponse> responses = new ArrayList<>();
        for (StockValueResponse value : values) {
            StockTicker stockTicker = stockTickerRepository.findByCode(value.code())
                    .orElseThrow(StockTickerNotFoundException::new);
            StockPriceResponse price = stockPriceFindService.findStockPriceAtDate(stockTicker, value.date());
            responses.add(StockValueRankingResponse.of(price, value));
        }
        return new StockValuesRankingResponse(responses);
    }

    private StockValuesResponse findTop20StockValues(String date) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8000/stock-values/ranking?date=" + date)
                .toUriString();
        return restTemplate.getForObject(url, StockValuesResponse.class);
    }

}
