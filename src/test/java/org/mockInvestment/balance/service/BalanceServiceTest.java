package org.mockInvestment.balance.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.util.ServiceTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class BalanceServiceTest extends ServiceTest {

    @Test
    @DisplayName("본인의 계좌 금액 조회한다.")
    void findBalance() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        CurrentBalanceResponse response = balanceService.findBalance(testAuthInfo);

        assertThat(response.balance()).isEqualTo(1000000.0);
    }

}