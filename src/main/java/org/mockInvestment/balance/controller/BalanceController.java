package org.mockInvestment.balance.controller;

import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.balance.service.BalanceService;
import org.mockInvestment.support.auth.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final BalanceService balanceService;


    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/balance/me")
    public ResponseEntity<CurrentBalanceResponse> findBalance(@Login AuthInfo authInfo) {
        CurrentBalanceResponse response = balanceService.findBalance(authInfo);
        return ResponseEntity.ok(response);
    }

}
