package org.mockInvestment.memberOwnStock.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.StockOrderType;
import org.mockInvestment.stockTicker.domain.StockTicker;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberOwnStockTest {

    private Member member;

    private StockTicker stockTicker;

    private MemberOwnStock memberOwnStock;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .role("USER")
                .username("USERNAME")
                .email("EMAIL")
                .build();
        stockTicker = new StockTicker("CODE", "NAME");
        memberOwnStock = new MemberOwnStock(1L, member, stockTicker);
    }

    @Test
    @DisplayName("구매 요청을 적용한다.")
    void applyBuyStockOrder() {
        StockOrder buyStockOrder = createStockOrder(true);

        memberOwnStock.apply(buyStockOrder, LocalDate.now());

        assertAll(
                () -> assertThat(memberOwnStock.getStockTicker()).isEqualTo(stockTicker),
                () -> assertThat(memberOwnStock.getQuantity()).isEqualTo(buyStockOrder.getQuantity()),
                () -> assertThat(memberOwnStock.getTotalValue()).isEqualTo(buyStockOrder.totalBidPrice()),
                () -> assertThat(memberOwnStock.getAverageCost()).isEqualTo(buyStockOrder.totalBidPrice() / buyStockOrder.getQuantity()),
                () -> assertThat(memberOwnStock.canDelete()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("판매 요청을 적용한다.")
    void applySellStockOrder() {
        StockOrder buyStockOrder = createStockOrder(true);
        memberOwnStock.apply(buyStockOrder, LocalDate.now());
        StockOrder sellStockOrder = createStockOrder(false);

        memberOwnStock.apply(sellStockOrder, LocalDate.now());

        assertAll(
                () -> assertThat(memberOwnStock.getStockTicker()).isEqualTo(stockTicker),
                () -> assertThat(memberOwnStock.getQuantity()).isEqualTo(0L),
                () -> assertThat(memberOwnStock.canDelete()).isEqualTo(true)
        );
    }

    private StockOrder createStockOrder(boolean buy) {
        return StockOrder.builder()
                .member(member)
                .stockTicker(stockTicker)
                .bidPrice(1.0)
                .quantity(1L)
                .stockOrderType(buy ? StockOrderType.BUY : StockOrderType.SELL)
                .orderDate(LocalDate.now())
                .build();
    }

}