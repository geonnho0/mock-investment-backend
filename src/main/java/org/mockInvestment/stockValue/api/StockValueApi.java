package org.mockInvestment.stockValue.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockValue.application.StockValueFindService;
import org.mockInvestment.stockValue.dto.StockValuesRankingResponse;
import org.mockInvestment.stockValue.dto.StockValuesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockValueApi {

    private final StockValueFindService stockValueFindService;


    @GetMapping("/stock-values")
    public ResponseEntity<StockValuesResponse> findStockValues(@RequestParam("code") String stockCode,
                                                               @RequestParam("date") String date) {
        StockValuesResponse response = stockValueFindService.findStockValuesByCode(stockCode, date);
        return ResponseEntity.ok(response);
    }

}
