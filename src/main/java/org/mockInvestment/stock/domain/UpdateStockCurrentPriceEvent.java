package org.mockInvestment.stock.domain;

public record UpdateStockCurrentPriceEvent(long stockId, String code, double curr) {
}
