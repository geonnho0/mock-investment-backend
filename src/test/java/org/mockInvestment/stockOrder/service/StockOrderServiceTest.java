package org.mockInvestment.stockOrder.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.advice.exception.*;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.StockOrderType;
import org.mockInvestment.stockOrder.dto.StockOrderCancelRequest;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
import org.mockInvestment.stockOrder.dto.StockOrderHistoriesResponse;
import org.mockInvestment.util.ServiceTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class StockOrderServiceTest extends ServiceTest {

    @Test
    @DisplayName("주식 주문 요청 생성")
    void createTestStockOrder() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");
        testStockOrder = createTestStockOrder(request.bidPrice(), request.volume());
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        when(stockOrderRepository.save(any(StockOrder.class)))
                .thenReturn(testStockOrder);

        stockOrderService.createStockOrder(testAuthInfo, "CODE", request);

        assertThat(testMember.getStockOrders().size()).isEqualTo(1);
        assertThat(testMember.getStockOrders().get(0).getMember().getId()).isEqualTo(testStockOrder.getMember().getId());
        assertThat(testMember.getStockOrders().get(0).getStock().getId()).isEqualTo(testStockOrder.getStock().getId());
    }

    @Test
    @DisplayName("사용자 정보가 유효하지 않으면 MemberNotFoundException 을 발생시킨다.")
    void createTestStockOrder_exception_invalidAuthInfo() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");
        when(memberRepository.findById(anyLong()))
                .thenThrow(new MemberNotFoundException());

        assertThatThrownBy(() -> stockOrderService.createStockOrder(testAuthInfo, "CODE", request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("주식 코드가 유효하지 않으면 StockNotFoundException 을 발생시킨다.")
    void createTestStockOrder_exception_invalidStockCode() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");
        when(memberRepository.findById(anyLong()))
                .thenThrow(new StockNotFoundException());

        assertThatThrownBy(() -> stockOrderService.createStockOrder(testAuthInfo, "CODE", request))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    @DisplayName("주식 주문 요청 취소")
    void cancelStockOrder() {
        testStockOrder = createTestStockOrder(1.0, 1L);
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testStockOrder));
        PendingStockOrder pendingStockOrder = new PendingStockOrder(1L, 1L, 1.0);
        when(pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(anyLong(), anyLong()))
                .thenReturn(Optional.of(pendingStockOrder));

        stockOrderService.cancelStockOrder(testAuthInfo, new StockOrderCancelRequest(1L));
    }

    @Test
    @DisplayName("주식 주문 요청 취소 시, 요청 id가 유효하지 않으면 StockOrderNotFoundException 을 발생시킨다.")
    void cancelStockOrder_exception_invalidStockOrderId() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockOrderRepository.findById(anyLong()))
                .thenThrow(new StockOrderNotFoundException());

        assertThatThrownBy(() -> stockOrderService.cancelStockOrder(testAuthInfo, new StockOrderCancelRequest(1L)))
                .isInstanceOf(StockOrderNotFoundException.class);
    }

    @Test
    @DisplayName("주식 주문 요청을 취소할 권한이 없으면 AuthorizationException 을 발생시킨다.")
    void cancelStockOrder_exception_authorization() {
        AuthInfo authInfo = new AuthInfo(2L, "USER", "USER", "USERNAME");
        testStockOrder = createTestStockOrder(1.0, 1L);
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testStockOrder));

        assertThatThrownBy(() -> stockOrderService.cancelStockOrder(authInfo, new StockOrderCancelRequest(1L)))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("대기중인 주식 주문 요청이 존재하지 않으면 PendingStockOrderNotFoundException 을 발생시킨다.")
    void cancelStockOrder_exception_pendingStockOrder_notFound() {
        testStockOrder = createTestStockOrder(1.0, 1L);
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockOrderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testStockOrder));
        when(pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(anyLong(), anyLong()))
                .thenThrow(new PendingStockOrderNotFoundException());

        assertThatThrownBy(() -> stockOrderService.cancelStockOrder(testAuthInfo, new StockOrderCancelRequest(1L)))
                .isInstanceOf(PendingStockOrderNotFoundException.class);
    }

    @Test
    @DisplayName("주식 주문 요청 기록 조회")
    void findStockOrderHistories() {
        List<StockOrder> stockOrders = new ArrayList<>();
        for (long i = 0; i < 5; i++)
            stockOrders.add(StockOrder.builder()
                    .id(i)
                    .stockOrderType(StockOrderType.BUY)
                    .member(testMember)
                    .stock(testStock)
                    .bidPrice(1.0)
                    .volume(i).build());
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockOrderRepository.findAllByMember(any(Member.class)))
                .thenReturn(stockOrders);

        StockOrderHistoriesResponse response = stockOrderService.findStockOrderHistories(1L);

        assertThat(response.histories().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("본인의 주문 요청 기록 조회")
    void findMyStockOrderHistoriesByCode() {
        List<StockOrder> stockOrders = new ArrayList<>();
        for (long i = 0; i < 5; i++)
            stockOrders.add(StockOrder.builder()
                    .id(i)
                    .stockOrderType(StockOrderType.BUY)
                    .member(testMember)
                    .stock(testStock)
                    .bidPrice(1.0)
                    .volume(i).build());
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        when(stockOrderRepository.findAllByMemberAndStock(any(Member.class), any(Stock.class)))
                .thenReturn(stockOrders);

        StockOrderHistoriesResponse response = stockOrderService.findMyStockOrderHistoriesByCode(testAuthInfo, "CODE");

        assertThat(response.histories().size()).isEqualTo(5);
    }

    private StockOrder createTestStockOrder(double bidPrice, long volume) {
        return StockOrder.builder()
                .id(1L)
                .member(testMember)
                .stock(testStock)
                .bidPrice(bidPrice)
                .volume(volume)
                .build();
    }

}