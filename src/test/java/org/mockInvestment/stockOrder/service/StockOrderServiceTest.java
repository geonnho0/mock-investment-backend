package org.mockInvestment.stockOrder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.advice.exception.*;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.dto.StockPurchaseCancelRequest;
import org.mockInvestment.stockOrder.dto.StockPurchaseRequest;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockOrderServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockOrderRepository stockOrderRepository;

    @Mock
    private PendingStockOrderCacheRepository pendingStockOrderCacheRepository;

    @InjectMocks
    private StockOrderService stockOrderService;

    private Member testMember;

    private AuthInfo testAuthInfo;

    private Stock testStock;

    private StockOrder testStockOrder;


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

    @Test
    @DisplayName("주식 구매 요청 생성")
    void requestStockPurchase() {
        StockPurchaseRequest request = new StockPurchaseRequest(1.0, 1L);
        testStockOrder = createStockOrder(request.bidPrice(), request.volume());
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        when(stockOrderRepository.save(any(StockOrder.class)))
                .thenReturn(testStockOrder);

        stockOrderService.requestStockPurchase(testAuthInfo, "CODE", request);

        assertThat(testMember.getStockOrders().size()).isEqualTo(1);
        assertThat(testMember.getStockOrders().get(0).getMember().getId()).isEqualTo(testStockOrder.getMember().getId());
        assertThat(testMember.getStockOrders().get(0).getStock().getId()).isEqualTo(testStockOrder.getStock().getId());
    }

    @Test
    @DisplayName("사용자 정보가 유효하지 않으면 MemberNotFoundException 을 발생시킨다.")
    void requestStockPurchase_exception_invalidAuthInfo() {
        when(memberRepository.findById(anyLong()))
                .thenThrow(new MemberNotFoundException());

        assertThatThrownBy(() -> stockOrderService.requestStockPurchase(testAuthInfo, "CODE", new StockPurchaseRequest(1.0, 1L)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("주식 코드가 유효하지 않으면 StockNotFoundException 을 발생시킨다.")
    void requestStockPurchase_exception_invalidStockCode() {
        when(memberRepository.findById(anyLong()))
                .thenThrow(new StockNotFoundException());

        assertThatThrownBy(() -> stockOrderService.requestStockPurchase(testAuthInfo, "CODE", new StockPurchaseRequest(1.0, 1L)))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    @DisplayName("주식 구매 요청을 취소한다.")
    void cancelStockPurchase() {
        testStockOrder = createStockOrder(1.0, 1L);
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testStockOrder));
        PendingStockOrder pendingStockOrder = new PendingStockOrder(1L, 1L, 1.0);
        when(pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(anyLong(), anyLong()))
                .thenReturn(Optional.of(pendingStockOrder));

        stockOrderService.cancelStockPurchase(testAuthInfo, new StockPurchaseCancelRequest(1L));
    }

    @Test
    @DisplayName("주식 구매 요청 id가 유효하지 않으면 StockOrderNotFoundException 을 발생시킨다.")
    void cancelStockPurchase_exception_invalidStockOrderId() {
        when(stockOrderRepository.findById(anyLong()))
                .thenThrow(new StockOrderNotFoundException());

        assertThatThrownBy(() -> stockOrderService.cancelStockPurchase(testAuthInfo, new StockPurchaseCancelRequest(1L)))
                .isInstanceOf(StockOrderNotFoundException.class);
    }

    @Test
    @DisplayName("주식 구매 요청을 취소할 권한이 없으면 AuthorizationException 을 발생시킨다.")
    void cancelStockPurchase_exception_authorization() {
        AuthInfo authInfo = new AuthInfo(2L, "USER", "USER", "USERNAME");
        testStockOrder = createStockOrder(1.0, 1L);
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testStockOrder));

        assertThatThrownBy(() -> stockOrderService.cancelStockPurchase(authInfo, new StockPurchaseCancelRequest(1L)))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("대기중인 주식 구매 요청이 존재하지 않으면 PendingStockOrderNotFoundException 을 발생시킨다.")
    void cancelStockPurchase_exception_pendingStockOrder_notFound() {
        testStockOrder = createStockOrder(1.0, 1L);
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testStockOrder));
        when(pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(anyLong(), anyLong()))
                .thenThrow(new PendingStockOrderNotFoundException());

        assertThatThrownBy(() -> stockOrderService.cancelStockPurchase(testAuthInfo, new StockPurchaseCancelRequest(1L)))
                .isInstanceOf(PendingStockOrderNotFoundException.class);
    }

    private StockOrder createStockOrder(double bidPrice, long volume) {
        return StockOrder.builder()
                .id(1L)
                .member(testMember)
                .stock(testStock)
                .bidPrice(bidPrice)
                .volume(volume)
                .build();
    }

}