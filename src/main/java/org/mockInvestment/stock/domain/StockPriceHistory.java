package org.mockInvestment.stock.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class StockPriceHistory {

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


    public StockPriceHistory(StockPrice price) {
        this.price = price;
    }
}
