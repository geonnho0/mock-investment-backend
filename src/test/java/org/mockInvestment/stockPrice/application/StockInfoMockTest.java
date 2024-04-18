package org.mockInvestment.stockPrice.application;

import org.mockInvestment.util.MockTest;

import static org.assertj.core.api.Assertions.assertThat;

class StockInfoMockTest extends MockTest {

//    @Test
//    @DisplayName("캐시에서 특정 주식의 최근 시세를 가져온다.")
//    void findStockInfo_byCache() {
//        when(recentStockInfoCacheRepository.findByStockCode(anyString()))
//                .thenReturn(Optional.ofNullable(testStockInfo));
//
//        StockTickerResponse response = stockInfoService.findStockInfo("CODE");
//
//        assertThat(response.base()).isEqualTo(testStockInfo.base());
//        assertThat(response.price()).isEqualTo(testStockInfo.curr());
//        assertThat(response.name()).isEqualTo(testStockInfo.name());
//        assertThat(response.symbol()).isEqualTo(testStockInfo.symbol());
//    }
//
//    @Test
//    @DisplayName("캐시에 특정 주식의 최근 시세가 존재하지 않다면, DB 에서 가져온다.")
//    void findStockInfo_byDB() {
//        when(recentStockInfoCacheRepository.findByStockCode(anyString()))
//                .thenReturn(Optional.empty());
//        when(stockRepository.findByCode(anyString()))
//                .thenReturn(Optional.ofNullable(testStock));
//        List<StockPriceCandle> candles = new ArrayList<>();
//        candles.add(new StockPriceCandle(testStock, new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0), 1L));
//        candles.add(new StockPriceCandle(testStock, new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0), 1L));
//        when(stockPriceCandleRepository.findTop2ByStockOrderByDateDesc(any(Stock.class)))
//                .thenReturn(candles);
//
//        StockTickerResponse response = stockInfoService.findStockInfo("CODE");
//
//        assertThat(response.name()).isEqualTo(testStock.getName());
//        assertThat(response.base()).isEqualTo(1.0);
//    }
//
//    @Test
//    @DisplayName("본인의 소유 주식들을 반환한다.")
//    void findMyOwnStocks() {
//        when(memberRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(testMember));
//        StockOrder stockOrder = StockOrder.builder().member(testMember).stockOrderType(StockOrderType.BUY).stock(testStock).bidPrice(1.0).volume(1L).build();
//        MemberOwnStock ownStock = MemberOwnStock.builder().id(1L).member(testMember).stock(testStock).stockOrder(stockOrder).build();
//        testMember.addOwnStock(ownStock);
//
//        MemberOwnStocksResponse response = stockInfoService.findMyOwnStocks(testAuthInfo, "CODE");
//
//        assertThat(response.stocks().size()).isEqualTo(1);
//        assertThat(response.stocks().get(0).name()).isEqualTo(ownStock.getStock().getName());
//    }

}