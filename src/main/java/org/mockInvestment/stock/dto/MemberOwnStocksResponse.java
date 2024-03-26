package org.mockInvestment.stock.dto;

import java.util.List;

public record MemberOwnStocksResponse(List<MemberOwnStockResponse> stocks) {
}
