package org.mockInvestment.stockOrder.repository;

import org.mockInvestment.stockOrder.domain.PendingStockOrder;

import java.util.List;

public interface PendingStockOrderCacheRepository {

    void save(PendingStockOrder entity);

    List<PendingStockOrder> findAllByStockCode(String stockCode);

    List<PendingStockOrder> findAllByMemberId(Long memberId);

    void delete(PendingStockOrder entity);

    void deleteById(Long stockOrderId);

    void deleteAll();

}
