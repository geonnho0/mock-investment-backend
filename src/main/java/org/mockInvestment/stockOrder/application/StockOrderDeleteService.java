package org.mockInvestment.stockOrder.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockOrderDeleteService {

    private final StockOrderRepository stockOrderRepository;

    private final PendingStockOrderCacheRepository pendingStockOrderCacheRepository;


    public void deleteStockOrder(long stockOrderId) {
        stockOrderRepository.deleteById(stockOrderId);
        pendingStockOrderCacheRepository.deleteById(stockOrderId);
    }

}
