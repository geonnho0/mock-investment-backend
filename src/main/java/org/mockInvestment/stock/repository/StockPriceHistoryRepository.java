package org.mockInvestment.stock.repository;

import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceHistoryRepository extends JpaRepository<StockPriceHistory, Long> {

    StockPriceHistory findFirstByStockOrderByDateDesc(Stock stock);

    List<StockPriceHistory> findAllByStockAndDateBetween(Stock stock, LocalDate startDate, LocalDate endDate);
}
