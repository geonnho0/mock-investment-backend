package org.mockInvestment.stockMomentum.dto;

public record StockMomentumResponse(int id, String code, String name, Double rateOfReturn, Double kRatio) {
}
