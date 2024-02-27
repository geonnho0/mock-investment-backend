package org.mockInvestment.stock.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
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
