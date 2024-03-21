package org.mockInvestment.stock.controller;

import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stock.dto.StockPriceCandlesResponse;
import org.mockInvestment.stock.dto.StockPricesResponse;
import org.mockInvestment.stock.service.StockPriceService;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.mockInvestment.support.auth.Login;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/stock-prices")
public class StockPriceController {


    private final StockPriceService stockPriceService;

    private final PeriodExtractor oneWeekPeriodExtractor;

    private final PeriodExtractor threeMonthsPeriodExtractor;

    private final PeriodExtractor oneYearPeriodExtractor;

    private final PeriodExtractor fiveYearsPeriodExtractor;

    public StockPriceController(StockPriceService stockPriceService, @Qualifier("oneWeekPeriodExtractor") PeriodExtractor oneWeekPeriodExtractor,
                                @Qualifier("threeMonthsPeriodExtractor") PeriodExtractor threeMonthsPeriodExtractor,
                                @Qualifier("oneYearPeriodExtractor") PeriodExtractor oneYearPeriodExtractor,
                                @Qualifier("fiveYearsPeriodExtractor") PeriodExtractor fiveYearsPeriodExtractor) {
        this.stockPriceService = stockPriceService;
        this.oneWeekPeriodExtractor = oneWeekPeriodExtractor;
        this.threeMonthsPeriodExtractor = threeMonthsPeriodExtractor;
        this.oneYearPeriodExtractor = oneYearPeriodExtractor;
        this.fiveYearsPeriodExtractor = fiveYearsPeriodExtractor;
    }

    @GetMapping
    public ResponseEntity<StockPricesResponse> findStockPrices(@RequestParam("code") List<String> stockCodes) {
        StockPricesResponse response = stockPriceService.findStockPrices(stockCodes);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@Login AuthInfo authInfo, @RequestParam("code") List<String> stockCodes) {
        SseEmitter sseEmitter = stockPriceService.subscribeStockPrices(authInfo, stockCodes);
        return ResponseEntity.ok(sseEmitter);
    }

    @GetMapping("/{code}/candles/1w")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForOneWeek(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockPriceService.findStockPriceCandles(stockCode, oneWeekPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}/candles/3m")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForThreeMonths(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockPriceService.findStockPriceCandles(stockCode, threeMonthsPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}/candles/1y")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForOneYear(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockPriceService.findStockPriceCandles(stockCode, oneYearPeriodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}/candles/5y")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceHistoriesForFiveYears(@PathVariable("code") String stockCode) {
        StockPriceCandlesResponse response = stockPriceService.findStockPriceCandles(stockCode, fiveYearsPeriodExtractor);
        return ResponseEntity.ok(response);
    }
}
