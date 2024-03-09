package org.mockInvestment.trade.repository;

import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.trade.domain.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {

    Optional<StockOrder> findById(Long id);

}
