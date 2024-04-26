package org.mockInvestment.stockPrice.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.global.auth.Login;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.dto.StockPriceCandlesResponse;
import org.mockInvestment.stockPrice.dto.StockPricesResponse;
import org.mockInvestment.stockPrice.application.StockPriceCandleFindService;
import org.mockInvestment.stockPrice.util.PeriodExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-prices")
public class StockPriceApi {

    private final StockPriceCandleFindService stockPriceCandleFindService;

    private final StockPriceFindService stockPriceFindService;


    @GetMapping
    public ResponseEntity<StockPricesResponse> findStockPricesAtDate(
            @RequestParam("code") List<String> stockCodes,
            @RequestParam("date") LocalDate date
    ) {
        StockPricesResponse response = stockPriceFindService.findStockPricesByCodeAtDate(stockCodes, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<StockPriceCandlesResponse> findStockPriceCandlesBetweenDate(
            @PathVariable("code") String stockCode,
            @RequestParam("end") LocalDate end,
            @RequestParam("period") String period
    ) {
        PeriodExtractor periodExtractor = new PeriodExtractor(end, period);
        StockPriceCandlesResponse response = stockPriceCandleFindService.findStockPriceCandles(stockCode, periodExtractor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/like")
    public ResponseEntity<StockPricesResponse> findAllLikedStockPricesAtDate(
            @RequestParam("date") LocalDate date,
            @Login AuthInfo authInfo
    ) {
        StockPricesResponse response = stockPriceFindService.findAllLikedStockPricesAtDate(date, authInfo);
        return ResponseEntity.ok(response);
    }

}
