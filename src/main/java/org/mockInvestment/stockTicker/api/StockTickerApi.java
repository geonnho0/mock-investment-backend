package org.mockInvestment.stockTicker.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.global.auth.Login;
import org.mockInvestment.stockTicker.application.StockTickerLikeToggleService;
import org.mockInvestment.stockTicker.dto.StockTickerLikeResponse;
import org.mockInvestment.stockTicker.dto.StockTickerResponse;
import org.mockInvestment.stockTicker.application.StockTickerFindService;
import org.mockInvestment.stockTicker.dto.StockTickersResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-ticker")
public class StockTickerApi {

    private final StockTickerFindService stockTickerFindService;

    private final StockTickerLikeToggleService stockTickerLikeToggleService;


    @GetMapping("/{code}")
    public ResponseEntity<StockTickerResponse> findStockTickerByCode(@PathVariable("code") String stockCode,
                                                                     @Login AuthInfo authInfo) {
        StockTickerResponse response = stockTickerFindService.findStockTickerByCode(stockCode, authInfo);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{code}/like")
    public ResponseEntity<StockTickerLikeResponse> toggleStockTickerLike(@PathVariable("code") String stockCode,
                                                                         @Login AuthInfo authInfo) {
        StockTickerLikeResponse response = stockTickerLikeToggleService.toggleLike(stockCode, authInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<StockTickersResponse> findStockTickersByKeyword(@RequestParam("keyword") String keyword,
                                                                         @Login AuthInfo authInfo) {
        StockTickersResponse response = stockTickerFindService.findStockTickersByKeyword(keyword, authInfo);
        return ResponseEntity.ok(response);
    }

}
