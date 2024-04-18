package org.mockInvestment.stockOrder.domain;

import lombok.Getter;
import org.mockInvestment.stockOrder.exception.InvalidStockOrderTypeException;

@Getter
public enum StockOrderType {

    BUY("BUY"), SELL("SELL");

    private final String value;


    StockOrderType(String value) {
        this.value = value;
    }

    public static StockOrderType parse(String value) {
        return switch (value) {
            case "BUY" -> BUY;
            case "SELL" -> SELL;
            default -> throw new InvalidStockOrderTypeException();
        };
    }

}
