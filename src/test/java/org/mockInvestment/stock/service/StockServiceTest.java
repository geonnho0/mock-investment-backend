package org.mockInvestment.stock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPrice;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.mockInvestment.stock.dto.StockPriceHistoriesResponse;
import org.mockInvestment.stock.repository.StockPriceHistoryRepository;
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
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockPriceHistoryRepository stockPriceHistoryRepository;

    @Mock
    private PeriodExtractor periodExtractor;


    @Test
    @DisplayName("유효한 코드로 현재 주가를 불러온다.")
    void findStockCurrentPrice() {
        when(stockRepository.findByCode(any(String.class)))
                .thenReturn(Optional.of(new Stock()));
        StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0);
        StockPriceHistory history = new StockPriceHistory(stockPrice, 1L);
        when(stockPriceHistoryRepository.findFirstByStockOrderByDateDesc(any(Stock.class)))
                .thenReturn(history);

        assertThat(stockService.findStockCurrentPrice("US1").price()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("유효하지 않은 코드로 현재 주가를 불러오면, StockNotFoundException을 발생시킨다.")
    void findStockCurrentPrice_exception_invalidCode() {
        when(stockRepository.findByCode(any(String.class)))
                .thenThrow(new StockNotFoundException());

        assertThatThrownBy(() -> stockService.findStockCurrentPrice("XXX"))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    @DisplayName("최근 3개월 동안의 주가 정보를 불러온다.")
    void findStockPriceHistoriesForThreeMonths() {
        when(stockRepository.findByCode(any(String.class)))
                .thenReturn(Optional.of(new Stock()));
        List<StockPriceHistory> histories = new ArrayList<>();
        StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0);
        int count = 10;
        for (int i = 0; i < count; i++) {
            histories.add(new StockPriceHistory(stockPrice, 1L));
        }
        when(stockPriceHistoryRepository.findAllByStockAndDateBetween(any(Stock.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(histories);
        when(periodExtractor.getStart())
                .thenReturn(LocalDate.now());
        when(periodExtractor.getEnd())
                .thenReturn(LocalDate.now());

        StockPriceHistoriesResponse response = stockService.findStockPriceHistories("US1", periodExtractor);

        assertThat(response.candles().size()).isEqualTo(count);
    }

}