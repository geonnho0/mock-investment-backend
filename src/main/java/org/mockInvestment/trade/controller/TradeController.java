package org.mockInvestment.trade.controller;

import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.support.auth.Login;
import org.mockInvestment.trade.dto.StockBuyRequest;
import org.mockInvestment.trade.service.TradeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradeController {

    private final TradeService tradeService;


    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/stocks/{code}/buy")
    public ResponseEntity<Void> buyStock(@Login AuthInfo authInfo, @PathVariable("code") String stockCode,
                                         @RequestBody StockBuyRequest request) {
        tradeService.buyStock(authInfo, stockCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
