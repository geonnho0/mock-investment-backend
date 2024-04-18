package org.mockInvestment.stockOrder.application;

import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.memberOwnStock.repository.MemberOwnStockRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockOrderFindMockTest extends MockTest {

    @Mock
    private MemberOwnStockRepository memberOwnStockRepository;

    @Mock
    private MemberOwnStock memberOwnStock;

//    @Test
//    @DisplayName("주식 주문 요청 생성")
//    void createTestStockOrder() {
//        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");
//        testStockOrder = createTestStockOrder(request.bidPrice(), request.volume());
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockRepository.findByCode(anyString()))
//                .thenReturn(Optional.ofNullable(testStock));
//        when(stockOrderRepository.save(any(StockOrder.class)))
//                .thenReturn(testStockOrder);
//
//        stockOrderFindService.createStockOrder(testAuthInfo, "CODE", request);
//
//        assertThat(testMember.getStockOrders().size()).isEqualTo(1);
//        assertThat(testMember.getStockOrders().get(0).getMember().getId()).isEqualTo(testStockOrder.getMember().getId());
//        assertThat(testMember.getStockOrders().get(0).getStock().getId()).isEqualTo(testStockOrder.getStock().getId());
//    }
//
//    @Test
//    @DisplayName("사용자 정보가 유효하지 않으면 MemberNotFoundException 을 발생시킨다.")
//    void createTestStockOrder_exception_invalidAuthInfo() {
//        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");
//        when(memberRepository.findById(anyLong()))
//                .thenThrow(new MemberNotFoundException());
//
//        assertThatThrownBy(() -> stockOrderFindService.createStockOrder(testAuthInfo, "CODE", request))
//                .isInstanceOf(MemberNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("주식 코드가 유효하지 않으면 StockNotFoundException 을 발생시킨다.")
//    void createTestStockOrder_exception_invalidStockCode() {
//        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");
//        when(memberRepository.findById(anyLong()))
//                .thenThrow(new StockNotFoundException());
//
//        assertThatThrownBy(() -> stockOrderFindService.createStockOrder(testAuthInfo, "CODE", request))
//                .isInstanceOf(StockNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("주식 주문 요청 취소")
//    void cancelStockOrder() {
//        testStockOrder = createTestStockOrder(1.0, 1L);
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockOrderRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testStockOrder));
//        PendingStockOrder pendingStockOrder = new PendingStockOrder(1L, 1L, 1.0);
//        when(pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(anyLong(), anyLong()))
//                .thenReturn(Optional.of(pendingStockOrder));
//
//        stockOrderFindService.cancelStockOrder(testAuthInfo, new StockOrderCancelRequest(1L));
//    }
//
//    @Test
//    @DisplayName("주식 주문 요청 취소 시, 요청 id가 유효하지 않으면 StockOrderNotFoundException 을 발생시킨다.")
//    void cancelStockOrder_exception_invalidStockOrderId() {
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockOrderRepository.findById(anyLong()))
//                .thenThrow(new StockOrderNotFoundException());
//
//        assertThatThrownBy(() -> stockOrderFindService.cancelStockOrder(testAuthInfo, new StockOrderCancelRequest(1L)))
//                .isInstanceOf(StockOrderNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("주식 주문 요청을 취소할 권한이 없으면 AuthorizationException 을 발생시킨다.")
//    void cancelStockOrder_exception_authorization() {
//        AuthInfo authInfo = new AuthInfo(2L, "USER", "USER", "USERNAME");
//        testStockOrder = createTestStockOrder(1.0, 1L);
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockOrderRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testStockOrder));
//
//        assertThatThrownBy(() -> stockOrderFindService.cancelStockOrder(authInfo, new StockOrderCancelRequest(1L)))
//                .isInstanceOf(AuthorizationException.class);
//    }
//
//    @Test
//    @DisplayName("대기중인 주식 주문 요청이 존재하지 않으면 PendingStockOrderNotFoundException 을 발생시킨다.")
//    void cancelStockOrder_exception_pendingStockOrder_notFound() {
//        testStockOrder = createTestStockOrder(1.0, 1L);
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockOrderRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testStockOrder));
//        when(pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(anyLong(), anyLong()))
//                .thenThrow(new PendingStockOrderNotFoundException());
//
//        assertThatThrownBy(() -> stockOrderFindService.cancelStockOrder(testAuthInfo, new StockOrderCancelRequest(1L)))
//                .isInstanceOf(PendingStockOrderNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("주식 주문 요청 기록 조회")
//    void findAllStockOrderHistories() {
//        List<StockOrder> stockOrders = new ArrayList<>();
//        for (long i = 0; i < 5; i++)
//            stockOrders.add(StockOrder.builder()
//                    .id(i)
//                    .stockOrderType(StockOrderType.BUY)
//                    .member(testMember)
//                    .stock(testStock)
//                    .bidPrice(1.0)
//                    .volume(i).build());
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockOrderRepository.findAllByMember(any(Member.class)))
//                .thenReturn(stockOrders);
//
//        StockOrderHistoriesResponse response = stockOrderFindService.findAllStockOrderHistories(1L);
//
//        assertThat(response.histories().size()).isEqualTo(5);
//    }
//
//    @Test
//    @DisplayName("본인의 특정 주식의 주문 요청 기록을 조회한다.")
//    void findMyStockOrderHistoriesByCode() {
//        List<StockOrder> stockOrders = new ArrayList<>();
//        for (long i = 0; i < 5; i++)
//            stockOrders.add(StockOrder.builder()
//                    .id(i)
//                    .stockOrderType(StockOrderType.BUY)
//                    .member(testMember)
//                    .stock(testStock)
//                    .bidPrice(1.0)
//                    .volume(i).build());
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockRepository.findByCode(anyString()))
//                .thenReturn(Optional.ofNullable(testStock));
//        when(stockOrderRepository.findAllByMemberAndStock(any(Member.class), any(Stock.class)))
//                .thenReturn(stockOrders);
//
//        StockOrderHistoriesResponse response = stockOrderFindService.findMyStockOrderHistoriesByCode(testAuthInfo, "CODE");
//
//        assertThat(response.histories().size()).isEqualTo(5);
//    }
//
//    @Test
//    @DisplayName("코드가 없으면, 본인의 모든 주문 요청 기록을 조회한다.")
//    void findMyStockOrderHistoriesByCode_emptyCode() {
//        List<StockOrder> stockOrders = new ArrayList<>();
//        for (long i = 0; i < 5; i++)
//            stockOrders.add(StockOrder.builder()
//                    .id(i)
//                    .stockOrderType(StockOrderType.BUY)
//                    .member(testMember)
//                    .stock(testStock)
//                    .bidPrice(1.0)
//                    .volume(i).build());
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        when(stockOrderRepository.findAllByMember(any(Member.class)))
//                .thenReturn(stockOrders);
//
//        StockOrderHistoriesResponse response = stockOrderFindService.findMyStockOrderHistoriesByCode(testAuthInfo, "");
//
//        assertThat(response.histories().size()).isEqualTo(5);
//    }
//
//    @Test
//    @DisplayName("대기중인 주문 요청 중, 처리 가능한 요청들을 처리한다.")
//    void executePendingStockOrders() {
//        when(testStockOrder.getMember()).thenReturn(testMember);
//        when(testStockOrder.getStock()).thenReturn(testStock);
//        when(testStockOrder.getBidPrice()).thenReturn(1.0);
//        when(testStockOrder.getQuantity()).thenReturn(1L);
//        PendingStockOrder pendingStockOrder = new PendingStockOrder(1L, 1L, 1.0);
//        List<PendingStockOrder> pendingStockOrders = new ArrayList<>();
//        pendingStockOrders.add(pendingStockOrder);
//        when(pendingStockOrderCacheRepository.findAllByStockId(anyLong()))
//                .thenReturn(pendingStockOrders);
//        when(stockOrderRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testStockOrder));
//        doNothing().when(memberOwnStock).apply(anyDouble(), anyLong(), anyBoolean());
//        when(memberOwnStockRepository.findByMemberAndStock(any(Member.class), any(Stock.class)))
//                .thenReturn(Optional.ofNullable(memberOwnStock));
//
//        UpdateStockCurrentPriceEvent event = new UpdateStockCurrentPriceEvent(1L, "CODE", 1.0);
//        stockOrderFindService.executePendingStockOrders(event);
//        verify(testStockOrder).execute();
//        verify(memberOwnStock).apply(testStockOrder.getBidPrice(), testStockOrder.getQuantity(), testStockOrder.isBuy());
//        verify(pendingStockOrderCacheRepository).remove(pendingStockOrder);
//    }
//
//    private StockOrder createTestStockOrder(double bidPrice, long volume) {
//        return StockOrder.builder()
//                .id(1L)
//                .member(testMember)
//                .stock(testStock)
//                .bidPrice(bidPrice)
//                .volume(volume)
//                .build();
//    }

}