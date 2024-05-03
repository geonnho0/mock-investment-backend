package org.mockInvestment.marketDate.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MarketDateFinder {

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private Map<LocalDate, LocalDate> marketDateCache = new HashMap<>();


    public LocalDate getNextMarketDate(LocalDate previousDate) {
        if (marketDateCache.containsKey(previousDate)) {
            return marketDateCache.remove(previousDate);
        }
        List<LocalDate> dates = stockPriceCandleRepository.findCandidateDates(previousDate, PageRequest.of(0, 2));
        LocalDate nextMarketDate = dates.get(dates.size() - 1);
        marketDateCache.put(previousDate, nextMarketDate);
        return nextMarketDate;
    }

}
