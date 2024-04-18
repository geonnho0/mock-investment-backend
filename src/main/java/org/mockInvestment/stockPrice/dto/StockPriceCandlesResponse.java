package org.mockInvestment.stockPrice.dto;

import java.util.List;

public record StockPriceCandlesResponse(String code, List<StockPriceCandleResponse> candles) {
}
