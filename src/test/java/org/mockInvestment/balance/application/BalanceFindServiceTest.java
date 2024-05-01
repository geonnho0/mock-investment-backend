package org.mockInvestment.balance.application;

import org.junit.jupiter.api.Test;
import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class BalanceFindServiceTest extends MockTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BalanceFindService balanceFindService;

    @Test
    void 본인의_계좌_금액을_조회한다() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        CurrentBalanceResponse response = balanceFindService.findBalance(testAuthInfo);

        assertThat(response.balance()).isEqualTo(Balance.DEFAULT_BALANCE);
    }

}