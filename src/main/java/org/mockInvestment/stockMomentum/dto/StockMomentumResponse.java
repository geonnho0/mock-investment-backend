package org.mockInvestment.stockMomentum.dto;

public record StockMomentumResponse(String code, String name, Double rateOfReturn, Double kRatio) {
}
