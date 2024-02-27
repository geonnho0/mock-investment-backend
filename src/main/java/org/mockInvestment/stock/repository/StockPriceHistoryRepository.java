package org.mockInvestment.stock.repository;

import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockPriceHistoryRepository extends JpaRepository<StockPriceHistory, Long> {

    @Query("select s from StockPriceHistory s where s.stock = :stock order by s.date desc limit 1")
    StockPriceHistory findStockCurrentPrice(Stock stock);
}
