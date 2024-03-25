package org.mockInvestment.stockOrder.controller;

import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stockOrder.dto.StockOrderHistoriesResponse;
import org.mockInvestment.support.auth.Login;
import org.mockInvestment.stockOrder.dto.StockPurchaseCancelRequest;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
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

    @PostMapping("/stocks/{code}/order")
    public ResponseEntity<Void> createStockOrder(@Login AuthInfo authInfo, @PathVariable("code") String stockCode,
                                                 @RequestBody NewStockOrderRequest request) {
        stockOrderService.createStockOrder(authInfo, stockCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/stocks/order")
    public ResponseEntity<Void> cancelStockStockOrder(@Login AuthInfo authInfo, @RequestBody StockPurchaseCancelRequest request) {
        stockOrderService.cancelStockPurchase(authInfo, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-orders/histories/me")
    public ResponseEntity<StockOrderHistoriesResponse> findMyStockOrderHistories(@Login AuthInfo authInfo) {
        StockOrderHistoriesResponse response = stockOrderService.findStockOrderHistories(authInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-orders/histories")
    public ResponseEntity<StockOrderHistoriesResponse> findStockOrderHistoriesByCode(@Login AuthInfo authInfo,
                                                                                 @RequestParam("code") String stockCode) {
        StockOrderHistoriesResponse response = stockOrderService.findStockOrderHistoriesByCode(authInfo, stockCode);
        return ResponseEntity.ok(response);
    }

}
