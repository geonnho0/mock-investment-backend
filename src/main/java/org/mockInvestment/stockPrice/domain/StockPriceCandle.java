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
@Table(name = "kor_price")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPriceCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "종목코드")
    private String stockTicker;

    @CreatedDate
    @Column(name = "날짜")
    private LocalDate date;

    @Embedded
    private StockPrice price;

    @Column(name = "거래량")
    private Long volume;


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
