package org.mockInvestment.memberOwnStock.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.global.auth.Login;
import org.mockInvestment.memberOwnStock.application.MemberOwnStockFindService;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStockValueResponse;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStocksResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class MemberOwnStockApi {

    private final MemberOwnStockFindService memberOwnStockFindService;


    @GetMapping("/member/me/own-stock")
    public ResponseEntity<MemberOwnStocksResponse> findMyOwnStocks(@Login AuthInfo authInfo,
                                                                   @RequestParam(value = "code", required = false, defaultValue = "") String stockCode) {
        MemberOwnStocksResponse response = memberOwnStockFindService.findMyOwnStocksFilteredByCode(authInfo, stockCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/me/own-stock/total-value")
    public ResponseEntity<MemberOwnStockValueResponse> findMyOwnStockValue(@Login AuthInfo authInfo,
                                                                           @RequestParam("date") LocalDate date) {
        MemberOwnStockValueResponse response = memberOwnStockFindService.findMyOwnStockTotalValue(authInfo, date);
        return ResponseEntity.ok(response);
    }

}
