package org.mockInvestment.stock.dto;

import java.util.List;

public record StockPriceCandlesResponse(String code, List<StockPriceCandleResponse> candles) {
}
