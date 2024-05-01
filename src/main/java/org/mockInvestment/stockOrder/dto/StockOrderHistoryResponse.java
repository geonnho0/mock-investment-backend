package org.mockInvestment.stockOrder.dto;


import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockTicker.domain.StockTicker;

import java.time.LocalDate;

public record StockOrderHistoryResponse(
        long id, LocalDate orderDate, String orderType, double bidPrice,
        long quantity, String name, String code, boolean executed, LocalDate executedDate
) {

    public static StockOrderHistoryResponse of(StockOrder entity) {
        StockTicker stockTicker = entity.getStockTicker();
        return new StockOrderHistoryResponse(entity.getId(),
                entity.getOrderDate(),
                entity.getStockOrderType().getValue(),
                entity.getBidPrice(),
                entity.getQuantity(),
                stockTicker.getName(),
                stockTicker.getCode(),
                entity.isExecuted(),
                entity.getExecutedDate());
    }

}
