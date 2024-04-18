package org.mockInvestment.balance.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.balance.application.BalanceService;
import org.mockInvestment.global.auth.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BalanceApi {

    private final BalanceService balanceService;


    @GetMapping("/balance/me")
    public ResponseEntity<CurrentBalanceResponse> findBalance(@Login AuthInfo authInfo) {
        CurrentBalanceResponse response = balanceService.findBalance(authInfo);
        return ResponseEntity.ok(response);
    }

}
