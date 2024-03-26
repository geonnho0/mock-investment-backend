package org.mockInvestment.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.service.BalanceService;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stock.domain.RecentStockInfo;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.repository.SseEmitterRepository;
import org.mockInvestment.stock.repository.RecentStockInfoCacheRepository;
import org.mockInvestment.stock.repository.StockPriceCandleRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stock.service.StockInfoService;
import org.mockInvestment.stock.service.StockPriceService;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockInvestment.stockOrder.service.StockOrderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    protected MemberRepository memberRepository;

    @Mock
    protected StockRepository stockRepository;

    @Mock
    protected StockOrderRepository stockOrderRepository;

    @Mock
    protected PendingStockOrderCacheRepository pendingStockOrderCacheRepository;

    @Mock
    protected RecentStockInfoCacheRepository recentStockInfoCacheRepository;

    @Mock
    protected StockPriceCandleRepository stockPriceCandleRepository;

    @Mock
    protected PeriodExtractor periodExtractor;

    @Mock
    protected SseEmitterRepository sseEmitterRepository;

    @InjectMocks
    protected BalanceService balanceService;

    @InjectMocks
    protected StockPriceService stockPriceService;

    @InjectMocks
    protected StockOrderService stockOrderService;

    @InjectMocks
    protected StockInfoService stockInfoService;

    protected Member testMember;

    protected AuthInfo testAuthInfo;

    protected Stock testStock;

    @Mock
    protected StockOrder testStockOrder;

    protected RecentStockInfo testStockInfo;


    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .role("USER")
                .name("NAME")
                .username("USERNAME")
                .email("EMAIL")
                .build();
        testAuthInfo = new AuthInfo(testMember);
        testStock = new Stock(1L, "CODE");
        testStockInfo = new RecentStockInfo("SYMBOL", "Stock name", 0.1,
                0.1, 1.0, 1.5, 0.6, 0.1, 10L);
    }

}
