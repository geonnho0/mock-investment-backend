package org.mockInvestment.stockPrice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "stock_price_candles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPriceCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private StockTicker stockTicker;

    private LocalDate date;

    @Embedded
    private StockPrice price;

    private Long volume;


    public StockPriceCandle(StockTicker stockTicker, StockPrice price, Long volume, LocalDate date) {
        this.stockTicker = stockTicker;
        this.price = price;
        this.volume = volume;
        this.date = date;
    }

    public Double getClose() {
        return price.getClose();
    }

    public Double getOpen() {
        return price.getOpen();
    }

    public Double getHigh() {
        return price.getHigh();
    }

    public Double getLow() {
        return price.getLow();
    }

}
