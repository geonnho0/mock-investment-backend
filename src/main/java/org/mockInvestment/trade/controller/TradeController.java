package org.mockInvestment.trade.controller;

import org.mockInvestment.trade.service.TradeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradeController {

    private final TradeService tradeService;


    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }
}
