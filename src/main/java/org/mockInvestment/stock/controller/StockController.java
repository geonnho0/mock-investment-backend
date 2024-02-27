package org.mockInvestment.stock.controller;

import org.mockInvestment.stock.dto.StockCurrentPriceResponse;
import org.mockInvestment.stock.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class StockController {

    private final StockService stockService;


    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stock-prices")
    public ResponseEntity<StockCurrentPriceResponse> findStockCurrentPrice(@RequestParam("code") String stockCode) {
        StockCurrentPriceResponse response = stockService.findStockCurrentPrice(stockCode);
        return ResponseEntity.ok(response);
    }
}
