package org.mockInvestment.stockOrder.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stockOrder.application.StockOrderCreateService;
import org.mockInvestment.stockOrder.application.StockOrderDeleteService;
import org.mockInvestment.stockOrder.dto.StockOrderHistoriesResponse;
import org.mockInvestment.global.auth.Login;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
import org.mockInvestment.stockOrder.application.StockOrderFindService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StockOrderApi {

    private final StockOrderFindService stockOrderFindService;

    private final StockOrderCreateService stockOrderCreateService;

    private final StockOrderDeleteService stockOrderDeleteService;


    @PostMapping("/stocks/{code}/order")
    public ResponseEntity<Void> createStockOrder(@Login AuthInfo authInfo,
                                                 @PathVariable("code") String stockCode,
                                                 @RequestBody NewStockOrderRequest request) {
        stockOrderCreateService.createStockOrder(authInfo, stockCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stocks/orders")
    public ResponseEntity<StockOrderHistoriesResponse> findAllStockOrderHistories(@RequestParam("member") long memberId) {
        StockOrderHistoriesResponse response = stockOrderFindService.findAllStockOrderHistories(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stocks/orders/me")
    public ResponseEntity<StockOrderHistoriesResponse> findMyStockOrderHistoriesByCode(@Login AuthInfo authInfo,
                                                                                       @RequestParam("code") String stockCode) {
        StockOrderHistoriesResponse response = stockOrderFindService.findMyStockOrderHistoriesByCode(authInfo, stockCode);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/stock-orders/{id}")
    public ResponseEntity<Void> cancelStockOrder(@PathVariable("id") long stockOrderId) {
        stockOrderDeleteService.deleteStockOrder(stockOrderId);
        return ResponseEntity.noContent().build();
    }

}
