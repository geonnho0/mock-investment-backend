package org.mockInvestment.stockPrice.dto;

import org.mockInvestment.stockTicker.domain.StockTicker;

public record StockPriceResponse(String code, String name, double base, double curr) {

    public static StockPriceResponse of(StockTicker stockTicker, RecentStockPrice recentStockPrice) {
        return new StockPriceResponse(stockTicker.getCode(), recentStockPrice.name(), recentStockPrice.base(), recentStockPrice.curr());
    }

}
