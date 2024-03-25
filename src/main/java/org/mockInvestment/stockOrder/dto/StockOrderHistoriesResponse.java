package org.mockInvestment.stockOrder.dto;

import java.util.List;

public record StockOrderHistoriesResponse(List<StockOrderHistoryResponse> histories) {
}
