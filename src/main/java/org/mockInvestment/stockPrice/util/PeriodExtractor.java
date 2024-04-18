package org.mockInvestment.stockPrice.util;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PeriodExtractor {

    private final LocalDate start;

    private final LocalDate end;


    public PeriodExtractor(LocalDate end, String period) {
        this.end = end;
        if (period.equals("6m"))
            start = end.minusMonths(6);
        else if (period.equals("1y"))
            start = end.minusYears(1);
        else
            start = end.minusYears(5);
    }

}
