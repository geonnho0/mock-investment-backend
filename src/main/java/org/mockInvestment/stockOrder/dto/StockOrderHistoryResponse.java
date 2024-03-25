package org.mockInvestment.stockOrder.dto;


import java.time.LocalDate;

public record StockOrderHistoryResponse(long id, LocalDate orderDate, String orderType, double bidPrice, long volume, String name) {
}
