package org.mockInvestment.stock.dto;

public record StockPriceResponse(String code, String name, double base, double curr) {

    public static StockPriceResponse of(String code, LastStockInfo lastStockInfo) {
        return new StockPriceResponse(code, lastStockInfo.name(), lastStockInfo.base(), lastStockInfo.curr());
    }
}
