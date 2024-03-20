package org.mockInvestment.stock.controller;

import org.mockInvestment.stock.dto.StockInfoResponse;
import org.mockInvestment.stock.service.StockInfoService;
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

}
