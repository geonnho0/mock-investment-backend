package org.mockInvestment.stockValue.dto;

import org.mockInvestment.stockValue.domain.StockValue;

import java.time.LocalDate;
import java.util.Map;

public record StockValueResponse(String code, LocalDate date, Double pbr, Double pcr, Double per, Double psr) {

    public static StockValueResponse of(StockValue stockValue) {
        return new StockValueResponse(stockValue.getStockTicker().getCode(),
                stockValue.getDate(),
                stockValue.getPbr(),
                stockValue.getPcr(),
                stockValue.getPer(),
                stockValue.getPsr());
    }

}
