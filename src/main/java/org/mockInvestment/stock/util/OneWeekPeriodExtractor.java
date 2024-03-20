package org.mockInvestment.stock.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Qualifier("oneWeekPeriodExtractor")
public class OneWeekPeriodExtractor implements PeriodExtractor {

    @Override
    public LocalDate getStart() {
        return getNow().minusWeeks(1).toLocalDate();
    }
}
