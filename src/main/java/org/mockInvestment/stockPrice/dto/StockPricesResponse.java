package org.mockInvestment.stockPrice.dto;

import java.util.List;

public record StockPricesResponse(List<StockPriceResponse> prices) {
}
