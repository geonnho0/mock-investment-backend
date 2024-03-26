package org.mockInvestment.stockOrder.repository;

import org.mockInvestment.stockOrder.domain.PendingStockOrder;

import java.util.List;
import java.util.Optional;

public interface PendingStockOrderCacheRepository {

    void save(PendingStockOrder entity);

    List<PendingStockOrder> findAllByStockId(long stockId);

    void remove(PendingStockOrder entity);

    Optional<PendingStockOrder> findByStockIdAndStockOrderId(long stockId, long stockOrderId);

}
