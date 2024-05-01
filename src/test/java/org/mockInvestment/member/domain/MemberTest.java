package org.mockInvestment.member.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.simulation.domain.MemberSimulationDate;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockTicker.domain.StockTickerLike;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private Member member;

    private Balance balance;

    private MemberSimulationDate simulationDate;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .role("USER")
                .username("USERNAME")
                .email("EMAIL")
                .build();
        balance = member.getBalance();
        simulationDate = member.getSimulationDate();
    }

    @Test
    void 예약_구매주문_적용() {
        PendingStockOrder buyStockOrder = new PendingStockOrder(1L, "CODE", 1L, true, 10.0, 10L);
        member.applyPendingStockOrder(buyStockOrder);

        assertThat(balance.getBalance()).isEqualTo(Balance.DEFAULT_BALANCE - buyStockOrder.totalBidPrice());
    }

    @Test
    void 예약_판매주문_적용() {
        PendingStockOrder sellStockOrder = new PendingStockOrder(1L, "CODE", 1L, false, 10.0, 10L);
        member.applyPendingStockOrder(sellStockOrder);

        assertThat(balance.getBalance()).isEqualTo(Balance.DEFAULT_BALANCE + sellStockOrder.totalBidPrice());
    }

    @Test
    void 닉네임_변경() {
        String nickname = "new nickname";
        member.updateNickname(nickname);

        assertThat(member.getNickname()).isEqualTo(nickname);
    }

    @Test
    void 동등성_확인() {
        Member saved = Member.builder()
                .id(1L)
                .build();

        assertThat(member.equals(saved)).isEqualTo(true);
    }

    @Test
    void 시뮬레이션_정보_초기화() {
        PendingStockOrder buyStockOrder = new PendingStockOrder(1L, "CODE", 1L, true, 10.0, 10L);
        member.applyPendingStockOrder(buyStockOrder);
        simulationDate.updateDate(LocalDate.now());

        member.resetSimulation();

        assertThat(balance.getBalance()).isEqualTo(Balance.DEFAULT_BALANCE);
        assertThat(simulationDate.getSimulationDate()).isEqualTo(MemberSimulationDate.DEFAULT_START_DATE);
    }

    @Test
    void 주식티커_좋아요() {
        StockTickerLike like = StockTickerLike.builder()
                .member(member)
                .build();
        member.addStockTickerLike(like);

        assertThat(member.getStockTickerLikes().size()).isEqualTo(1);
    }

    @Test
    void 주식티커_좋아요_취소() {
        StockTickerLike like = StockTickerLike.builder()
                .member(member)
                .build();
        member.addStockTickerLike(like);

        member.deleteStockTickerLike(like);

        assertThat(member.getStockTickerLikes().size()).isEqualTo(0);
    }

}