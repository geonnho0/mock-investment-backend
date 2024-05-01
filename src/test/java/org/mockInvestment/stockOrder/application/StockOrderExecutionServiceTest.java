package org.mockInvestment.stockOrder.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.memberOwnStock.repository.MemberOwnStockRepository;
import org.mockInvestment.simulation.domain.SimulationProceedInfo;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.StockOrderType;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockOrderExecutionServiceTest extends MockTest {

    @Mock
    private MemberOwnStockRepository memberOwnStockRepository;

    @Mock
    private PendingStockOrderCacheRepository pendingStockOrderCacheRepository;

    @Mock
    private StockPriceFindService stockPriceFindService;

    @Mock
    private StockOrderRepository stockOrderRepository;

    @Mock
    private StockTickerRepository stockTickerRepository;

    @InjectMocks
    private StockOrderExecutionService stockOrderExecutionService;

    private MemberOwnStock memberOwnStock;

    private StockTicker stockTicker;

    private StockOrder buyStockOrder, sellStockOrder;


    @BeforeEach
    void setUp() {
        stockTicker = new StockTicker("CODE", "NAME");
        memberOwnStock = MemberOwnStock.builder()
                .member(testMember)
                .stockTicker(stockTicker)
                .build();
        buyStockOrder = StockOrder.builder()
                .id(1L)
                .stockOrderType(StockOrderType.BUY)
                .member(testMember)
                .stockTicker(stockTicker)
                .bidPrice(1.0)
                .quantity(1L)
                .build();
        sellStockOrder = StockOrder.builder()
                .id(2L)
                .stockOrderType(StockOrderType.SELL)
                .member(testMember)
                .stockTicker(stockTicker)
                .bidPrice(1.0)
                .quantity(1L)
                .build();
    }


    @Test
    void 처리가능한_예약된_구매_요청을_처리() {
        when(pendingStockOrderCacheRepository.findAllByMemberId(anyLong()))
                .thenReturn(createPendingStockOrders(true));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.of(new StockTicker("Code", "Name")));
        when(stockPriceFindService.findRecentStockPriceAtDate(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(new RecentStockPrice("Code", "Name", 0.9, 0.6, 1.1, 0.7));
        when(memberOwnStockRepository.save(any(MemberOwnStock.class)))
                .thenReturn(memberOwnStock);
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(buyStockOrder));

        stockOrderExecutionService.checkAndApplyPendingStock(new SimulationProceedInfo(LocalDate.now(), testMember));

        assertAll(
                () -> assertThat(memberOwnStock.getQuantity()).isEqualTo(1),
                () -> assertThat(memberOwnStock.getAverageCost()).isEqualTo(buyStockOrder.getBidPrice()),
                () -> assertThat(testMember.getBalance().getBalance()).isEqualTo(Balance.DEFAULT_BALANCE - buyStockOrder.totalBidPrice()),
                () -> assertThat(testMember.getOwnStocks().size()).isEqualTo(1)
        );
    }

    @Test
    void 처리가능한_예약된_판매_요청을_처리() {
        when(pendingStockOrderCacheRepository.findAllByMemberId(anyLong()))
                .thenReturn(createPendingStockOrders(false));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.of(new StockTicker("Code", "Name")));
        when(stockPriceFindService.findRecentStockPriceAtDate(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(new RecentStockPrice("Code", "Name", 0.9, 0.6, 1.1, 0.7));
        when(memberOwnStockRepository.findByMemberAndStockTicker(any(Member.class), any(StockTicker.class)))
                .thenReturn(Optional.ofNullable(memberOwnStock));
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(sellStockOrder));

        setSellStockOrder();
        stockOrderExecutionService.checkAndApplyPendingStock(new SimulationProceedInfo(LocalDate.now(), testMember));

        assertAll(
                () -> verify(memberOwnStockRepository).delete(memberOwnStock),
                () -> assertThat(testMember.getBalance().getBalance()).isEqualTo(Balance.DEFAULT_BALANCE),
                () -> assertThat(testMember.getOwnStocks().isEmpty()).isEqualTo(true)
        );
    }

    @Test
    void 처리불가능한_주문_요청은_처리하지_않음() {
        when(pendingStockOrderCacheRepository.findAllByMemberId(anyLong()))
                .thenReturn(createPendingStockOrders(true));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.of(new StockTicker("Code", "Name")));
        when(stockPriceFindService.findRecentStockPriceAtDate(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(new RecentStockPrice("Code", "Name", 1.9, 1.6, 2.1, 1.5));

        stockOrderExecutionService.checkAndApplyPendingStock(new SimulationProceedInfo(LocalDate.now(), testMember));
    }

    private List<PendingStockOrder> createPendingStockOrders(boolean buy) {
        List<PendingStockOrder> pendingStockOrders = new ArrayList<>();
        List<StockOrder> stockOrders = createStockOrders(buy);
        for (StockOrder stockOrder : stockOrders) {
            pendingStockOrders.add(PendingStockOrder.of(stockOrder, testMember));
        }
        return pendingStockOrders;
    }

    private List<StockOrder> createStockOrders(boolean buy) {
        List<StockOrder> stockOrders = new ArrayList<>();
        for (long i = 0; i < 1; i++) {
            stockOrders.add(buy ? buyStockOrder : sellStockOrder);
        }
        return stockOrders;
    }

    private void setSellStockOrder() {
        List<PendingStockOrder> pendingStockOrders = createPendingStockOrders(true);
        testMember.applyPendingStockOrder(pendingStockOrders.get(0));
        memberOwnStock.apply(buyStockOrder, LocalDate.now());
    }

}