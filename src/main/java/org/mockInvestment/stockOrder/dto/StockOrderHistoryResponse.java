package org.mockInvestment.stockOrder.dto;


import org.mockInvestment.stockOrder.domain.StockOrder;

import java.time.LocalDate;

public record StockOrderHistoryResponse(long id, LocalDate orderDate, String orderType, double bidPrice, long volume, String name) {

    public static StockOrderHistoryResponse of(StockOrder entity) {
        return new StockOrderHistoryResponse(entity.getId(),
                entity.getOrderDate(),
                entity.getStockOrderType().getValue(),
                entity.getBidPrice(),
                entity.getVolume(),
                entity.getStock().getName());
    }

}
