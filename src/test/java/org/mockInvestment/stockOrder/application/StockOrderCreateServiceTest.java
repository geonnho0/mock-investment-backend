package org.mockInvestment.stockOrder.application;

import org.junit.jupiter.api.Test;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockOrderCreateServiceTest extends MockTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StockOrderRepository stockOrderRepository;

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private PendingStockOrderCacheRepository stockOrderCacheRepository;

    @InjectMocks
    private StockOrderCreateService stockOrderCreateService;


    @Test
    void 주식_주문_요청_생성() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY", LocalDate.now());
        StockOrder stockOrder = createTestStockOrder(request.bidPrice(), request.quantity());
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStockTicker));
        when(stockOrderRepository.save(any(StockOrder.class)))
                .thenReturn(stockOrder);

        stockOrderCreateService.createStockOrder(testAuthInfo, "CODE", request);

        verify(stockOrderCacheRepository).save(PendingStockOrder.of(stockOrder, testMember));
    }

    private StockOrder createTestStockOrder(double bidPrice, long quantity) {
        StockTicker stockTicker = new StockTicker("CODE", "NAME");

        return StockOrder.builder()
                .id(1L)
                .member(testMember)
                .stockTicker(stockTicker)
                .bidPrice(bidPrice)
                .quantity(quantity)
                .build();
    }

}