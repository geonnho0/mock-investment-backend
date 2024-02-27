package org.mockInvestment.stock.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class StockPrice {

    private Double open;

    private Double high;

    private Double low;

    private Double close;

    private Double curr;

}
