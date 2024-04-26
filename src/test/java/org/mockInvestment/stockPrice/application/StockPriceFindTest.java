package org.mockInvestment.stockPrice.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockPrice.domain.StockPrice;
import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;
import org.mockInvestment.stockPrice.dto.StockPriceResponse;
import org.mockInvestment.stockPrice.dto.StockPricesResponse;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.domain.StockTickerLike;
import org.mockInvestment.stockTicker.repository.StockTickerLikeRepository;
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
import static org.mockito.Mockito.when;

class StockPriceFindTest extends MockTest {

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private StockPriceCandleRepository stockPriceCandleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StockTickerLikeRepository stockTickerLikeRepository;

    @InjectMocks
    private StockPriceFindService stockPriceFindService;

    private StockTicker stockTicker;

    @BeforeEach
    void setUp() {
        stockTicker = new StockTicker("CODE", "NAME");
    }


    @Test
    @DisplayName("특정 주식(들)의 특정 날짜의 시세를 가져온다.")
    void findStockPricesAtDate() {
        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("Code");
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(stockTicker));
        when(stockPriceCandleRepository.findTop2ByStockTickerAndDateLessThanEqualOrderByDateDesc(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(createStockPriceCandles());

        StockPricesResponse response = stockPriceFindService.findStockPricesByCodeAtDate(stockCodes, LocalDate.now());

        assertAll(
                () -> assertThat(response.prices().size()).isEqualTo(1),
                () -> assertThat(response.prices().get(0).code()).isEqualTo(stockTicker.getCode()),
                () -> assertThat(response.prices().get(0).base()).isEqualTo(1.0)
        );
    }

    @Test
    @DisplayName("즐겨찾기한 주식들의 특정 날짜의 가격을 가져온다.")
    void findAllLikedStockPricesAtDate() {
        List<StockTickerLike> stockTickerLikes = new ArrayList<>();
        stockTickerLikes.add(new StockTickerLike(stockTicker, testMember));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerLikeRepository.findAllByMember(any(Member.class)))
                .thenReturn(stockTickerLikes);
        when(stockPriceCandleRepository.findTop2ByStockTickerAndDateLessThanEqualOrderByDateDesc(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(createStockPriceCandles());

        StockPricesResponse response = stockPriceFindService.findAllLikedStockPricesAtDate(LocalDate.now(), testAuthInfo);

        assertAll(
                () -> assertThat(response.prices().size()).isEqualTo(1),
                () -> assertThat(response.prices().get(0).code()).isEqualTo(stockTicker.getCode()),
                () -> assertThat(response.prices().get(0).base()).isEqualTo(1.0)
        );
    }

    @Test
    @DisplayName("특정 주식의 특정 날짜와 전날의 가격을 가져온다.")
    void findRecentStockPriceAtDate() {
        when(stockPriceCandleRepository.findTop2ByStockTickerAndDateLessThanEqualOrderByDateDesc(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(createStockPriceCandles());

        RecentStockPrice price = stockPriceFindService.findRecentStockPriceAtDate(stockTicker, LocalDate.now());

        assertAll(
                () -> assertThat(price.base()).isEqualTo(1.0),
                () -> assertThat(price.code()).isEqualTo(stockTicker.getCode())
        );
    }

    @Test
    @DisplayName("특정 주식의 특정 날짜의 가격을 가져온다.")
    void findStockPriceAtDate() {
        when(stockPriceCandleRepository.findTop2ByStockTickerAndDateLessThanEqualOrderByDateDesc(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(createStockPriceCandles());

        StockPriceResponse response = stockPriceFindService.findStockPriceAtDate(stockTicker, LocalDate.now());

        assertAll(
                () -> assertThat(response.code()).isEqualTo(stockTicker.getCode()),
                () -> assertThat(response.base()).isEqualTo(1.0)
        );
    }

    private List<StockPriceCandle> createStockPriceCandles() {
        List<StockPriceCandle> candles = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0);
            candles.add(new StockPriceCandle(stockTicker, stockPrice, i * 1000, LocalDate.now()));
        }
        return candles;
    }

}