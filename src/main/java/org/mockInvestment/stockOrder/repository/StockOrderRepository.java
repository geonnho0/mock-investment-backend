package org.mockInvestment.stockOrder.repository;

import org.mockInvestment.stockOrder.domain.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {

    Optional<StockOrder> findById(Long id);

}
