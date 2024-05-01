package org.mockInvestment.stockPrice.repository;

import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockPriceCandleRepository extends JpaRepository<StockPriceCandle, Long> {

    Optional<StockPriceCandle> findByStockTickerAndDate(StockTicker stockTicker, LocalDate date);

    List<StockPriceCandle> findTop2ByStockTickerAndDateLessThanEqualOrderByDateDesc(StockTicker stockTicker, LocalDate date);

    List<StockPriceCandle> findAllByStockTickerAndDateBetween(StockTicker stockTicker, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT DISTINCT spc.date FROM StockPriceCandle spc WHERE spc.date >= :date ORDER BY spc.date")
    List<LocalDate> findCandidateDates(LocalDate date, Pageable pageable);

}
