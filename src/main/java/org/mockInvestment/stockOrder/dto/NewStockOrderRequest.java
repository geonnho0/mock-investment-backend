package org.mockInvestment.stockOrder.dto;


import java.time.LocalDate;

public record NewStockOrderRequest(double bidPrice, long quantity, String orderType, LocalDate orderDate) {
}
