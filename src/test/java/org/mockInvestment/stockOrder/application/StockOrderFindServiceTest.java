package org.mockInvestment.stockOrder.application;

import org.junit.jupiter.api.Test;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.StockOrderType;
import org.mockInvestment.stockOrder.dto.StockOrderHistoriesResponse;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class StockOrderFindServiceTest extends MockTest {

    @Mock
    private StockOrderRepository stockOrderRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StockTickerRepository stockTickerRepository;

    @InjectMocks
    private StockOrderFindService stockOrderFindService;


    @Test
    void 주식_주문_요청_기록_조회() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockOrderRepository.findAllByMember(any(Member.class)))
                .thenReturn(createStockOrders(10));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.of(new StockTicker("code", "name")));

        StockOrderHistoriesResponse response = stockOrderFindService.findAllStockOrderHistories(1L);

        assertThat(response.histories().size()).isEqualTo(10);
    }

    @Test
    void 본인의_특정_주식_주문_요청_기록_조회() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.of(new StockTicker("code", "name")));
        when(stockOrderRepository.findAllByMemberAndStockTicker(any(Member.class), anyString()))
                .thenReturn(createStockOrders(10));

        StockOrderHistoriesResponse response = stockOrderFindService.findMyStockOrderHistoriesByCode(testAuthInfo, "CODE");

        assertThat(response.histories().size()).isEqualTo(10);
    }

    @Test
    void 본인의_모든_주문_요청_기록_조회() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockOrderRepository.findAllByMember(any(Member.class)))
                .thenReturn(createStockOrders(10));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.of(new StockTicker("code", "name")));

        StockOrderHistoriesResponse response = stockOrderFindService.findMyStockOrderHistoriesByCode(testAuthInfo, "");

        assertThat(response.histories().size()).isEqualTo(10);
    }

    private List<StockOrder> createStockOrders(long count) {
        StockTicker stockTicker = new StockTicker("CODE", "NAME");
        List<StockOrder> stockOrders = new ArrayList<>();
        for (long i = 0; i < count; i++)
            stockOrders.add(StockOrder.builder()
                    .id(i)
                    .stockOrderType(i % 2 == 0 ? StockOrderType.BUY : StockOrderType.SELL)
                    .member(testMember)
                    .stockTicker(stockTicker)
                    .bidPrice(1.0)
                    .quantity(i)
                    .build());
        return stockOrders;
    }

}