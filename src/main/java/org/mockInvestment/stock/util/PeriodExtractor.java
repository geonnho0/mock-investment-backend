package org.mockInvestment.stock.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class PeriodExtractor {

    protected ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));

    public abstract LocalDate getStart();

    public LocalDate getEnd() {
        return now.toLocalDate();
    }
}
