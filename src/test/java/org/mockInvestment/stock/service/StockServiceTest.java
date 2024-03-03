package org.mockInvestment.stock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPrice;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.mockInvestment.stock.repository.StockPriceHistoryRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


    @Test
    @DisplayName("유효한 코드로 현재 주가를 불러온다.")
    void findStockCurrentPrice() {
        when(stockRepository.findByCode(any(String.class)))
                .thenReturn(Optional.of(new Stock()));
        StockPriceHistory history = new StockPriceHistory(StockPrice.builder().curr(1.0).build());
        when(stockPriceHistoryRepository.findStockCurrentPrice(any(Stock.class)))
                .thenReturn(history);

        assertThat(stockService.findStockCurrentPrice("US1").getPrice()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("유효하지 않은 코드로 현재 주가를 불러오면, StockNotFoundException을 발생시킨다.")
    void findStockCurrentPrice_exception_invalidCode() {
        when(stockRepository.findByCode(any(String.class)))
                .thenThrow(new StockNotFoundException());

        assertThatThrownBy(() -> stockService.findStockCurrentPrice("XXX"))
                .isInstanceOf(StockNotFoundException.class);
    }

}