package org.mockInvestment.stock.dto;

import java.util.List;

public record StockPriceHistoriesResponse(String code, List<StockPriceHistoryResponse> candles) {
}
