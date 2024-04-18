package org.mockInvestment.stockPrice.dto;

public record RecentStockPrice(String code, String name, double curr, double base, double high, double low) {
}
