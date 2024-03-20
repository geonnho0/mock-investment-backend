package org.mockInvestment.stockOrder.controller;

import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.support.auth.Login;
import org.mockInvestment.stockOrder.dto.StockPurchaseCancelRequest;
import org.mockInvestment.stockOrder.dto.StockPurchaseRequest;
import org.mockInvestment.stockOrder.service.StockOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StockOrderController {

    private final StockOrderService stockOrderService;


    public StockOrderController(StockOrderService stockOrderService) {
        this.stockOrderService = stockOrderService;
    }

    @PostMapping("/stocks/{code}/purchase")
    public ResponseEntity<Void> requestStockPurchase(@Login AuthInfo authInfo, @PathVariable("code") String stockCode,
                                         @RequestBody StockPurchaseRequest request) {
        stockOrderService.requestStockPurchase(authInfo, stockCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/stocks/purchase")
    public ResponseEntity<Void> cancelStockPurchase(@Login AuthInfo authInfo, @RequestBody StockPurchaseCancelRequest request) {
        stockOrderService.cancelStockPurchase(authInfo, request);
        return ResponseEntity.noContent().build();
    }

}
