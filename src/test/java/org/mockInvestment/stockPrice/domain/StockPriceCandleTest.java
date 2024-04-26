package org.mockInvestment.stockPrice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StockPriceCandleTest {

    @Test
    @DisplayName("시가, 고가, 저가, 종가")
    void getPrice() {
        StockPrice stockPrice = new StockPrice(1.1, 1.2, 0.9, 1.0);

        assertAll(
                () -> assertThat(stockPrice.getOpen()).isEqualTo(1.1),
                () -> assertThat(stockPrice.getHigh()).isEqualTo(1.2),
                () -> assertThat(stockPrice.getLow()).isEqualTo(0.9),
                () -> assertThat(stockPrice.getClose()).isEqualTo(1.0)
        );
    }

}