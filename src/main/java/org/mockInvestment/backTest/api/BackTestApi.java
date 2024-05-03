package org.mockInvestment.backTest.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.backTest.application.BackTestCrossStrategyService;
import org.mockInvestment.backTest.application.BackTestRSIStrategyService;
import org.mockInvestment.backTest.dto.request.CrossStrategyRequest;
import org.mockInvestment.backTest.dto.request.StrategyRequest;
import org.mockInvestment.backTest.dto.response.BackTestResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back_test")
public class BackTestApi {

    private final BackTestRSIStrategyService backTestRSIStrategyService;

    private final BackTestCrossStrategyService backTestCrossStrategyService;


    @GetMapping("/rsi")
    public ResponseEntity<BackTestResultResponse> backTestUsingRSIStrategy(
            @RequestParam("code") String stockCode,
            @RequestParam("start") LocalDate startDate,
            @RequestParam("end") LocalDate endDate,
            @RequestParam("buyRSI") double buyRSI,
            @RequestParam("sellRSI") double sellRSI,
            @RequestParam("amount") double amount
    ) {
        StrategyRequest request = new StrategyRequest(stockCode, startDate, endDate, buyRSI, sellRSI, amount);
        BackTestResultResponse response = backTestRSIStrategyService.runTest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cross")
    public ResponseEntity<BackTestResultResponse> backTestUsingCrossStrategy(
            @RequestParam("code") String stockCode,
            @RequestParam("start") LocalDate startDate,
            @RequestParam("end") LocalDate endDate,
            @RequestParam("amount") double amount
    ) {
        StrategyRequest request = new StrategyRequest(stockCode, startDate, endDate, 0, 0, amount);
        BackTestResultResponse response = backTestCrossStrategyService.runTest(request);
        return ResponseEntity.ok(response);
    }

}
