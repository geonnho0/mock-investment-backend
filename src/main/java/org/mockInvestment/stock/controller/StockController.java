package org.mockInvestment.stock.controller;

import org.mockInvestment.stock.dto.StockCurrentPriceResponse;
import org.mockInvestment.stock.dto.StockPriceHistoriesResponse;
import org.mockInvestment.stock.service.StockService;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/stock-prices")
    public ResponseEntity<StockCurrentPriceResponse> findStockCurrentPrice(@RequestParam("code") String stockCode) {
        StockCurrentPriceResponse response = stockService.findStockCurrentPrice(stockCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/1w")
    public ResponseEntity<StockPriceHistoriesResponse> findStockPriceHistoriesForOneWeek(@PathVariable("code") String stockCode) {
        StockPriceHistoriesResponse response = stockService.findStockPriceHistories(stockCode, oneWeekPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/3m")
    public ResponseEntity<StockPriceHistoriesResponse> findStockPriceHistoriesForThreeMonths(@PathVariable("code") String stockCode) {
        StockPriceHistoriesResponse response = stockService.findStockPriceHistories(stockCode, threeMonthsPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/1y")
    public ResponseEntity<StockPriceHistoriesResponse> findStockPriceHistoriesForOneYear(@PathVariable("code") String stockCode) {
        StockPriceHistoriesResponse response = stockService.findStockPriceHistories(stockCode, oneYearPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-prices/{code}/candles/5y")
    public ResponseEntity<StockPriceHistoriesResponse> findStockPriceHistoriesForFiveYears(@PathVariable("code") String stockCode) {
        StockPriceHistoriesResponse response = stockService.findStockPriceHistories(stockCode, fiveYearsPeriodExtractor);
        return ResponseEntity.ok(response);
    }
}
