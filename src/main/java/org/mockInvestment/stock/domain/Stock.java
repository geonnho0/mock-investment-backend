package org.mockInvestment.stock.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mockInvestment.stockOrder.domain.StockOrder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String symbol;

    private String name;

    @OneToMany(mappedBy = "stock")
    private List<StockPriceCandle> priceHistories;

    @OneToMany(mappedBy = "stock")
    private List<StockOrder> orders;

    public Stock(Long id, String code) {
        this.id = id;
        this.code = code;
    }

}
