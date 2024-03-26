package org.mockInvestment.stock.dto;

import java.util.List;

public record StockPricesResponse(List<StockPriceResponse> prices) {
}
