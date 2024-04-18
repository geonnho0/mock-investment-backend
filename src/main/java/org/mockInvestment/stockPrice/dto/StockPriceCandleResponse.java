package org.mockInvestment.stockPrice.dto;


import org.mockInvestment.stockPrice.domain.StockPriceCandle;

import java.time.LocalDate;

public record StockPriceCandleResponse(LocalDate dt, double o, double c, double l, double h, long v) {

    public StockPriceCandleResponse(StockPriceCandle entity) {
        this(entity.getDate(), entity.getPrice().getOpen(), entity.getPrice().getClose(),
             entity.getPrice().getLow(), entity.getPrice().getHigh(), entity.getVolume());
    }

}