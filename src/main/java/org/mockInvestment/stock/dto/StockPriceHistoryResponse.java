package org.mockInvestment.stock.dto;


import org.mockInvestment.stock.domain.StockPriceHistory;

import java.time.LocalDate;

public record StockPriceHistoryResponse (LocalDate dt, double o, double c, double l, double h, long v) {

    public StockPriceHistoryResponse(StockPriceHistory entity) {
        this(entity.getDate(), entity.getPrice().getOpen(), entity.getPrice().getClose(),
             entity.getPrice().getLow(), entity.getPrice().getHigh(), entity.getVolume());
    }
}