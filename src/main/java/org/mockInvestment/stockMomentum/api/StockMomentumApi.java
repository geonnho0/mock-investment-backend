package org.mockInvestment.stockMomentum.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockMomentum.application.StockMomentumFindService;
import org.mockInvestment.stockMomentum.dto.StockMomentumsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockMomentumApi {

    private final StockMomentumFindService stockMomentumFindService;


    @GetMapping("/stock-momentums")
    public ResponseEntity<StockMomentumsResponse> findStockMomentums(@RequestParam("code") List<String> stockCodes,
                                                                     @RequestParam("date") String date) {
        StockMomentumsResponse response = stockMomentumFindService.findStockMomentumsByCodesAtDate(stockCodes, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock-momentums/all")
    public ResponseEntity<StockMomentumsResponse> findStockMomentumsRanking(@RequestParam("date") String date) {
        StockMomentumsResponse response = stockMomentumFindService.findStockMomentumsRankingAtDate(date);
        return ResponseEntity.ok(response);
    }

}
