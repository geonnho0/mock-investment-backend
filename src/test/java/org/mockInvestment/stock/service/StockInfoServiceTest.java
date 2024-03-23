package org.mockInvestment.stock.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.advice.exception.InvalidStockCodeException;
import org.mockInvestment.stock.domain.RecentStockInfo;
import org.mockInvestment.stock.dto.StockInfoResponse;
import org.mockInvestment.stock.repository.RecentStockInfoCacheRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockInfoServiceTest {

    @InjectMocks
    private StockInfoService stockInfoService;

    @Mock
    private RecentStockInfoCacheRepository recentStockInfoCacheRepository;

    RecentStockInfo testStockInfo;

    @BeforeEach
    void setUp() {
        testStockInfo = new RecentStockInfo("MOCK", "Mock Stock", 0.1, 0.1, 1.0, 1.5, 0.6, 0.1, 10L);
    }

    @Test
    @DisplayName("유효한 코드로 특정 주식의 현재 주가에 대한 자세한 정보를 불러온다.")
    void findStockInfoDetail() {
        when(recentStockInfoCacheRepository.findByStockCode(any(String.class)))
                .thenReturn(Optional.ofNullable(testStockInfo));

        StockInfoResponse response = stockInfoService.findStockInfo("Mock Stock");

        assertThat(response.base()).isEqualTo(0.1);
        assertThat(response.price()).isEqualTo(1.0);
        assertThat(response.name()).isEqualTo("Mock Stock");
    }

    @Test
    @DisplayName("유효하지 않은 코드로 특정 주식의 현재 주가에 대한 자세한 정보를 요청하면, InvalidStockCodeException 을 발생시킨다.")
    void findStockInfoDetail_exception_invalidCode() {
        when(recentStockInfoCacheRepository.findByStockCode(any(String.class)))
                .thenThrow(new InvalidStockCodeException());

        assertThatThrownBy(() -> stockInfoService.findStockInfo("XX"))
                .isInstanceOf(InvalidStockCodeException.class);
    }

}