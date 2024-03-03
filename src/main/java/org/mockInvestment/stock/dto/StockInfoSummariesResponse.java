package org.mockInvestment.stock.dto;

import java.util.List;

public record StockInfoSummariesResponse(List<StockInfoSummaryResponse> stocks) {
}
