package org.mockInvestment.stock.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class StockPrice {

    private Double open;

    private Double high;

    private Double low;

    private Double close;

    private Double curr;

    public StockPrice(Double open, Double high, Double low, Double close, Double curr) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.curr = curr;
    }

}
