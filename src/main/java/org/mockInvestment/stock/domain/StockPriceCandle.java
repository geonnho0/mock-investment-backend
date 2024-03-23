package org.mockInvestment.stock.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class StockPriceCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Stock stock;

    @CreatedDate
    private LocalDate date;

    @Embedded
    private StockPrice price;

    private Long volume;


    public StockPriceCandle(StockPrice price, long volume) {
        this.price = price;
        this.volume = volume;
    }

    public Double getClose() {
        return price.getClose();
    }

    public Double getCurr() {
        return price.getCurr();
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
