package org.mockInvestment.stockOrder.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockOrder.exception.InvalidStockOrderException;
import org.mockInvestment.stockTicker.domain.StockTicker;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class StockOrderTest {

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
                .quantity(5L)
                .build();
        sellStockOrder = StockOrder.builder()
                .id(2L)
                .stockOrderType(StockOrderType.SELL)
                .member(member)
                .stockTicker(stockTicker)
                .bidPrice(1.0)
                .quantity(5L)
                .build();
    }

    @Test
    void 총_소모금액() {
        assertThat(buyStockOrder.totalBidPrice()).isEqualTo(buyStockOrder.getQuantity() * buyStockOrder.getBidPrice());
    }

    @Test
    void 주문_타입() {
        assertAll(
                () -> assertThat(buyStockOrder.isBuy()).isEqualTo(true),
                () -> assertThat(sellStockOrder.isBuy()).isEqualTo(false)
        );
    }

    @Test
    void 판매수량이_보유한_주식수량보다_많으면_예외발생() {
        Long ownedQuantity = 2L;
        assertThatThrownBy(() -> sellStockOrder.checkQuantity(ownedQuantity))
                .isInstanceOf(InvalidStockOrderException.class);
    }

    @Test
    void 주문_실행() {
        buyStockOrder.execute(LocalDate.now());

        assertAll(
                () -> assertThat(buyStockOrder.isExecuted()).isEqualTo(true),
                () -> assertThat(buyStockOrder.getExecutedDate()).isEqualTo(LocalDate.now())
        );
    }
}