package org.mockInvestment.stockTicker.dto;

import java.util.List;

public record StockTickersResponse(List<StockTickerResponse> stockTickers) {
}
