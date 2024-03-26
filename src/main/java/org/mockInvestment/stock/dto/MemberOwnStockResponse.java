package org.mockInvestment.stock.dto;


import org.mockInvestment.stock.domain.MemberOwnStock;

public record MemberOwnStockResponse(long id, double averageCost, long volume, String code, String symbol, String name) {

    public static MemberOwnStockResponse of(MemberOwnStock entity) {
        return new MemberOwnStockResponse(entity.getId(),
                entity.getAverageCost(),
                entity.getVolume(),
                entity.getStock().getCode(),
                entity.getStock().getSymbol(),
                entity.getStock().getName());
    }

}
