package org.mockInvestment.stock.controller;

import org.mockInvestment.stock.dto.StockInfoDetailResponse;
import org.mockInvestment.stock.dto.StockPriceCandlesResponse;
import org.mockInvestment.stock.dto.StockInfoSummariesResponse;
import org.mockInvestment.stock.service.StockService;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "*")
@RestController
public class StockController {

    private final StockService stockService;

    private final PeriodExtractor oneWeekPeriodExtractor;

    private final PeriodExtractor threeMonthsPeriodExtractor;

    private final PeriodExtractor oneYearPeriodExtractor;

    private final PeriodExtractor fiveYearsPeriodExtractor;

    public StockController(StockService stockService, @Qualifier("oneWeekPeriodExtractor") PeriodExtractor oneWeekPeriodExtractor,
                           @Qualifier("threeMonthsPeriodExtractor") PeriodExtractor threeMonthsPeriodExtractor,
                           @Qualifier("oneYearPeriodExtractor") PeriodExtractor oneYearPeriodExtractor,
                           @Qualifier("fiveYearsPeriodExtractor") PeriodExtractor fiveYearsPeriodExtractor) {
        this.stockService = stockService;
        this.oneWeekPeriodExtractor = oneWeekPeriodExtractor;
        this.threeMonthsPeriodExtractor = threeMonthsPeriodExtractor;
        this.oneYearPeriodExtractor = oneYearPeriodExtractor;
        this.fiveYearsPeriodExtractor = fiveYearsPeriodExtractor;
    }

    @GetMapping("/stock-detail/{code}/common")
    public ResponseEntity<StockInfoDetailResponse> findStockInfoDetail(@PathVariable("code") String stockCode) {
        StockInfoDetailResponse response = stockService.findStockInfoDetail(stockCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices")
    public ResponseEntity<StockInfoSummariesResponse> findStockSummaries(@RequestParam("code") List<String> stockCodes) {
        StockInfoSummariesResponse response = stockService.findStockInfoSummaries(stockCodes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/1w")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForOneWeek(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockService.findStockPriceHistories(stockCode, oneWeekPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/3m")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForThreeMonths(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockService.findStockPriceHistories(stockCode, threeMonthsPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/1y")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForOneYear(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockService.findStockPriceHistories(stockCode, oneYearPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/5y")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForFiveYears(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockService.findStockPriceHistories(stockCode, fiveYearsPeriodExtractor);
        return ResponseEntity.ok(response);
    }
}
