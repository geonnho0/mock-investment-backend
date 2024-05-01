package org.mockInvestment.stockOrder.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;
import org.mockInvestment.stockTicker.domain.StockTicker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PendingStockOrderTest {

    private Member member;

    private StockTicker stockTicker;

    private StockOrder buyStockOrder, sellStockOrder;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .role("USER")
                .username("USERNAME")
                .email("EMAIL")
                .build();
        stockTicker = new StockTicker("Code", "Name");
        buyStockOrder = StockOrder.builder()
                .id(1L)
                .stockOrderType(StockOrderType.BUY)
                .member(member)
                .stockTicker(stockTicker)
                .bidPrice(1.0)
                .quantity(1L)
                .build();
        sellStockOrder = StockOrder.builder()
                .id(2L)
                .stockOrderType(StockOrderType.SELL)
                .member(member)
                .stockTicker(stockTicker)
                .bidPrice(1.0)
                .quantity(1L)
                .build();
    }

    @Test
    void 유저가_주문한_요청인지_확인() {
        PendingStockOrder pendingStockOrder = PendingStockOrder.of(buyStockOrder, member);
        Member another = Member.builder().id(2L).build();

        assertAll(
                () -> assertThat(pendingStockOrder.orderedBy(member.getId())).isEqualTo(true),
                () -> assertThat(pendingStockOrder.orderedBy(another.getId())).isEqualTo(false)
        );
    }

    @Test
    void 실행할수_있는_요청인지_확인() {
        PendingStockOrder pendingStockOrder = PendingStockOrder.of(buyStockOrder, member);
        RecentStockPrice canExecute = new RecentStockPrice("Code", "Name", 1.0, 0.5, 1.5, 0.5);
        RecentStockPrice cannotExecute = new RecentStockPrice("Code", "Name", 2.0, 1.5, 2.5, 1.5);

        assertAll(
                () -> assertThat(pendingStockOrder.cannotExecute(canExecute)).isEqualTo(false),
                () -> assertThat(pendingStockOrder.cannotExecute(cannotExecute)).isEqualTo(true)
        );
    }

    @Test
    void 주문_타입() {
        PendingStockOrder buyStockOrder = PendingStockOrder.of(this.buyStockOrder, member);
        PendingStockOrder sellStockOrder = PendingStockOrder.of(this.sellStockOrder, member);

        assertAll(
                () -> assertThat(buyStockOrder.stockOrderType()).isEqualTo(StockOrderType.BUY),
                () -> assertThat(buyStockOrder.isBuy()).isEqualTo(true),
                () -> assertThat(sellStockOrder.stockOrderType()).isEqualTo(StockOrderType.SELL),
                () -> assertThat(sellStockOrder.isBuy()).isEqualTo(false)
        );
    }

    @Test
    void 총_소모금액() {
        PendingStockOrder pendingStockOrder = PendingStockOrder.of(buyStockOrder, member);

        assertThat(pendingStockOrder.totalBidPrice()).isEqualTo(pendingStockOrder.quantity() * pendingStockOrder.bidPrice());
    }

}