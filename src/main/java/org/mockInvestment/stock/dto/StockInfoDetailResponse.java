package org.mockInvestment.stock.dto;

import org.mockInvestment.stock.domain.Stock;

public record StockInfoDetailResponse(String name, String symbol, double base, double price) {
}
