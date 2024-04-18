package org.mockInvestment.stockPrice.dto;

public record StockPriceResponse(String code, String name, double base, double curr) {

    public static StockPriceResponse of(String code, RecentStockPrice recentStockPrice) {
        return new StockPriceResponse(code, recentStockPrice.name(), recentStockPrice.base(), recentStockPrice.curr());
    }

}
