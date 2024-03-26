package org.mockInvestment.stock.controller;

import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stock.dto.MemberOwnStocksResponse;
import org.mockInvestment.stock.dto.StockInfoResponse;
import org.mockInvestment.stock.service.StockInfoService;
import org.mockInvestment.support.auth.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class StockInfoController {

    private final StockInfoService stockInfoService;


    public StockInfoController(StockInfoService stockInfoService) {
        this.stockInfoService = stockInfoService;
    }

    @GetMapping("/stock-info/{code}")
    public ResponseEntity<StockInfoResponse> findStockInfo(@PathVariable("code") String stockCode) {
        StockInfoResponse response = stockInfoService.findStockInfo(stockCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/me/own-stock")
    public ResponseEntity<MemberOwnStocksResponse> findMyOwnStocks(@Login AuthInfo authInfo,
                                                                   @RequestParam(value = "code", defaultValue = "") String stockCode) {
        MemberOwnStocksResponse response = stockInfoService.findMyOwnStocks(authInfo, stockCode);
        return ResponseEntity.ok(response);
    }

}
