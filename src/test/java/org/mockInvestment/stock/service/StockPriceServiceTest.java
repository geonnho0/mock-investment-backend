package org.mockInvestment.stock.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.advice.exception.InvalidStockCodeException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPrice;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.mockInvestment.stock.dto.LastStockInfo;
import org.mockInvestment.stock.dto.StockPriceCandlesResponse;
import org.mockInvestment.stock.repository.LastStockInfoCacheRepository;
import org.mockInvestment.stock.repository.StockPriceCandleRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockPriceServiceTest {

    @Mock
    private LastStockInfoCacheRepository lastStockInfoCacheRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockPriceCandleRepository stockPriceCandleRepository;

    @Mock
    private PeriodExtractor periodExtractor;

    @InjectMocks
    private StockPriceService stockPriceService;

    private LastStockInfo testStockInfo;

    @BeforeEach
    void setUp() {
        testStockInfo = new LastStockInfo("MOCK", "Mock Stock", 0.1, 0.1, 1.0, 1.5, 0.6, 0.1, 10L);
    }

    @Test
    @DisplayName("유효한 코드로 현재 주가(들)에 대한 간략한 정보를 불러온다.")
    void findStockInfoSummaries() {
        when(lastStockInfoCacheRepository.findByStockCode(any(String.class)))
                .thenReturn(Optional.ofNullable(testStockInfo));

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("US1");
        stockCodes.add("US2");
        assertThat(stockPriceService.findStockPrices(stockCodes).prices().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("유효하지 않은 코드로 현재 주가(들)에 대한 간략한 정보를 불려오려고 하면, InvalidStockCodeException 을 발생시킨다.")
    void findStockCurrentPrice_exception_invalidCodes() {
        when(lastStockInfoCacheRepository.findByStockCode(any(String.class)))
                .thenThrow(new InvalidStockCodeException());

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("XXX");
        assertThatThrownBy(() -> stockPriceService.findStockPrices(stockCodes))
                .isInstanceOf(InvalidStockCodeException.class);
    }

    @Test
    @DisplayName("최근 3개월 동안의 주가 정보를 불러온다.")
    void findStockPriceCandles_3Months() {
        when(stockRepository.findByCode(any(String.class)))
                .thenReturn(Optional.of(new Stock()));
        List<StockPriceCandle> priceCandles = new ArrayList<>();
        StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0);
        int count = 10;
        for (int i = 0; i < count; i++) {
            priceCandles.add(new StockPriceCandle(stockPrice, 1L));
        }
        when(stockPriceCandleRepository.findAllByStockAndDateBetween(any(Stock.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(priceCandles);
        when(periodExtractor.getStart())
                .thenReturn(LocalDate.now());
        when(periodExtractor.getEnd())
                .thenReturn(LocalDate.now());

        StockPriceCandlesResponse response = stockPriceService.findStockPriceCandles("US1", periodExtractor);

        assertThat(response.candles().size()).isEqualTo(count);
    }
}