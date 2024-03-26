package org.mockInvestment.stockOrder.dto;


public record NewStockOrderRequest(double bidPrice, long volume, String orderType) {
}
