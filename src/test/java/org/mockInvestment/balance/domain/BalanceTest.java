package org.mockInvestment.balance.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockInvestment.balance.exception.PaymentFailureException;
import org.mockInvestment.member.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceTest {

    private Balance balance;

    private Member member;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .role("USER")
                .username("USERNAME")
                .email("EMAIL")
                .build();
        balance = member.getBalance();
    }

    @Test
    void 현재_금액_조회() {
        assertThat(balance.getBalance()).isEqualTo(Balance.DEFAULT_BALANCE);
    }

    @Test
    void 결제() {
        balance.pay(10.0);

        assertThat(balance.getBalance()).isEqualTo(Balance.DEFAULT_BALANCE - 10.0);
    }

    @Test
    void 남은_금액이_결제액보다_적다면_예외_발생() {
        assertThatThrownBy(() -> balance.pay(Balance.DEFAULT_BALANCE + 10.0))
                .isInstanceOf(PaymentFailureException.class);
    }

    @Test
    void 환불() {
        balance.receive(10.0);

        assertThat(balance.getBalance()).isEqualTo(Balance.DEFAULT_BALANCE + 10.0);
    }

    @Test
    void 계좌_초기화() {
        balance.pay(10.0);
        balance.pay(20.0);
        balance.reset();

        assertThat(balance.getBalance()).isEqualTo(Balance.DEFAULT_BALANCE);
    }

}