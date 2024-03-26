package org.mockInvestment.stock.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface PeriodExtractor {

    LocalDate getStart();

    default ZonedDateTime getNow() {
        return ZonedDateTime.now(ZoneId.of("America/New_York"));
    }

    default LocalDate getEnd() {
        return getNow().toLocalDate();
    }
}
