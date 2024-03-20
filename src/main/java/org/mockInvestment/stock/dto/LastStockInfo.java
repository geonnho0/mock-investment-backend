package org.mockInvestment.stock.dto;


public record LastStockInfo(String symbol, String name, double base, double close, double curr, double high, double low, double open, long volume) {
}