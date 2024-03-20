package org.mockInvestment.stock.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Qualifier("oneYearPeriodExtractor")
public class OneYearPeriodExtractor implements PeriodExtractor {

    @Override
    public LocalDate getStart() {
        return getNow().minusYears(1).toLocalDate();
    }

}
