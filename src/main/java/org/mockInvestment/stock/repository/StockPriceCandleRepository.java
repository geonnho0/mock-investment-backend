package org.mockInvestment.stock.repository;

import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceCandleRepository extends JpaRepository<StockPriceCandle, Long> {

    List<StockPriceCandle> findAllByStockAndDateBetween(Stock stock, LocalDate startDate, LocalDate endDate);

}
