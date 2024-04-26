package org.mockInvestment.stockPrice.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.StockOrderType;
import org.mockInvestment.stockPrice.domain.StockPrice;
import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.mockInvestment.stockPrice.dto.StockPriceCandlesResponse;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.mockInvestment.stockPrice.util.PeriodExtractor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class StockPriceCandleFindServiceTest extends MockTest {

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private StockPriceCandleRepository stockPriceCandleRepository;

    @InjectMocks
    private StockPriceCandleFindService stockPriceCandleFindService;

    private StockTicker stockTicker;


    @BeforeEach
    void setUp() {
        stockTicker = new StockTicker("CODE", "NAME");;
    }

    @Test
    @DisplayName("특정 주식의 특정 날짜로부터 최근 1년의 가격 정보를 가져온다.")
    void findStockPriceCandles() {
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(stockTicker));
        when(stockPriceCandleRepository.findAllByStockTickerAndDateBetween(
                any(StockTicker.class), any(LocalDate.class), any(LocalDate.class))
        ).thenReturn(createStockPriceCandles());

        LocalDate end = LocalDate.now();
        StockPriceCandlesResponse response = stockPriceCandleFindService
                .findStockPriceCandles(stockTicker.getCode(), new PeriodExtractor(end, "1y"));

        assertAll(
                () -> assertThat(response.code()).isEqualTo(stockTicker.getCode()),
                () -> assertThat(response.candles().size()).isEqualTo(5)
        );
    }

    private List<StockPriceCandle> createStockPriceCandles() {
        List<StockPriceCandle> candles = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0);
            candles.add(new StockPriceCandle(stockTicker, stockPrice, i * 1000, LocalDate.now()));
        }
        return candles;
    }

}