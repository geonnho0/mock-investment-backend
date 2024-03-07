package org.mockInvestment.stock.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String symbol;

    private String name;

    @OneToMany(mappedBy = "stock")
    private List<StockPriceHistory> priceHistories;

}
