package org.mockInvestment.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.service.BalanceService;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.repository.StockRepository;
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

    @InjectMocks
    protected StockOrderService stockOrderService;

    @InjectMocks
    protected BalanceService balanceService;

    protected Member testMember;

    protected AuthInfo testAuthInfo;

    protected Stock testStock;

    protected StockOrder testStockOrder;


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
    }

}
