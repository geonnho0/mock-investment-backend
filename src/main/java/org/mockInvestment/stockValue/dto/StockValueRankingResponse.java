package org.mockInvestment.stockValue.dto;


import org.mockInvestment.stockPrice.dto.StockPriceResponse;

import java.time.LocalDate;

public record StockValueRankingResponse(String code, String name, Double base, Double curr, LocalDate date,
                                        Double pbr, Double pcr, Double per, Double psr) {

    public static StockValueRankingResponse of(StockPriceResponse priceResponse, StockValueResponse valueResponse) {
        return new StockValueRankingResponse(priceResponse.code(), priceResponse.name(), priceResponse.base(), priceResponse.curr(),
                valueResponse.date(), valueResponse.pbr(), valueResponse.pcr(), valueResponse.per(), valueResponse.psr());
    }

}
