package org.mockInvestment.stock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StockCurrentPriceResponse {

    private final double price;

    @Builder
    public StockCurrentPriceResponse(double price) {
        this.price = price;
    }
}
