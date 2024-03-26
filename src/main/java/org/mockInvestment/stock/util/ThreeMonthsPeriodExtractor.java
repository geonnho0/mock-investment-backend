package org.mockInvestment.stock.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Qualifier("threeMonthsPeriodExtractor")
public class ThreeMonthsPeriodExtractor implements PeriodExtractor {
    @Override
    public LocalDate getStart() {
        return getNow().minusMonths(3).toLocalDate();
    }
}
