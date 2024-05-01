package org.mockInvestment.stockOrder.application;

import org.junit.jupiter.api.Test;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class StockOrderDeleteServiceTest extends MockTest {

    @Mock
    private StockOrderRepository stockOrderRepository;

    @Mock
    private PendingStockOrderCacheRepository pendingStockOrderCacheRepository;

    @InjectMocks
    private StockOrderDeleteService stockOrderDeleteService;


    @Test
    void 주문_요청_삭제() {
        stockOrderDeleteService.deleteStockOrder(1L);

        verify(stockOrderRepository).deleteById(1L);
        verify(pendingStockOrderCacheRepository).deleteById(1L);
    }

}