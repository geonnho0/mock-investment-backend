package org.mockInvestment.memberOwnStock.dto;


import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.stockTicker.domain.StockTicker;

public record MemberOwnStockResponse(long id, double averageCost, long quantity, String code, String name) {

    public static MemberOwnStockResponse of(MemberOwnStock entity, StockTicker stockTicker) {
        return new MemberOwnStockResponse(entity.getId(),
                entity.getAverageCost(),
                entity.getQuantity(),
                stockTicker.getCode(),
                stockTicker.getName());
    }

}
