package org.mockInvestment.stock.dto;

import org.mockInvestment.stock.domain.Stock;

public record StockInfoDetailResponse(String name, String symbol, double base, double price) {
    public StockInfoDetailResponse(Stock entity, double base, double price) {
        this(entity.getName(), entity.getSymbol(), base, price);
    }
}
