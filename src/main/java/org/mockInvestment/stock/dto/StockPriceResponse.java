package org.mockInvestment.stock.dto;

import org.mockInvestment.stock.domain.RecentStockInfo;

public record StockPriceResponse(String code, String name, double base, double curr) {

    public static StockPriceResponse of(String code, RecentStockInfo recentStockInfo) {
        return new StockPriceResponse(code, recentStockInfo.name(), recentStockInfo.base(), recentStockInfo.curr());
    }
}
