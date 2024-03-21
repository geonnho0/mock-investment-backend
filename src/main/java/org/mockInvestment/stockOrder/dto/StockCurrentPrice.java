package org.mockInvestment.stockOrder.dto;

public record StockCurrentPrice(long stockId, String code, double curr) {
}
