package org.mockInvestment.stockValue.dto;

import java.time.LocalDate;
import java.util.Map;

public record StockValueResponse(String code, LocalDate date, Double pbr, Double pcr, Double per, Double psr) {

    public static StockValueResponse of(String code, LocalDate date, Map<String, Double> values) {
        return new StockValueResponse(code, date, values.get("PBR"), values.get("PCR"), values.get("PER"), values.get("PSR"));
    }

}
