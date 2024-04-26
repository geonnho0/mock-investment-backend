package org.mockInvestment.stockValue.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mockInvestment.stockTicker.domain.StockTicker;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "stock_values")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private StockTicker stockTicker;

    private LocalDate date;

    private Double per;

    private Double pbr;

    private Double pcr;

    private Double psr;

}
