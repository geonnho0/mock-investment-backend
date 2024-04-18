package org.mockInvestment.stockValue.dto;

import java.util.List;

public record StockValuesResponse(List<StockValueResponse> values) {
}
