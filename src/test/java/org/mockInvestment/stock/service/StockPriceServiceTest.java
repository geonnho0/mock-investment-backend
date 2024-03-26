package org.mockInvestment.stock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.advice.exception.InvalidStockCodeException;
import org.mockInvestment.advice.exception.SseEmitterEventSendException;
import org.mockInvestment.advice.exception.SseEmitterNotFoundException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPrice;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.mockInvestment.stock.domain.UpdateStockCurrentPriceEvent;
import org.mockInvestment.stock.dto.StockPriceCandlesResponse;
import org.mockInvestment.stock.dto.StockPricesResponse;
import org.mockInvestment.util.ServiceTest;
import org.mockito.Mock;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StockPriceServiceTest extends ServiceTest {

    @Mock
    private SseEmitter sseEmitter;

    @Test
    @DisplayName("특정 주식(들)의 현재 시세를 요청 시, 캐시에서 값들을 가져온다.")
    void findStockPrices_byCache() {
        when(recentStockInfoCacheRepository.findByStockCode(anyString()))
                .thenReturn(Optional.ofNullable(testStockInfo));

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("US1");
        stockCodes.add("US2");

        StockPricesResponse response = stockPriceService.findStockPrices(stockCodes);

        assertThat(response.prices().size()).isEqualTo(2);
        assertThat(response.prices().get(0).code()).isEqualTo("US1");
        assertThat(response.prices().get(1).code()).isEqualTo("US2");
    }

    @Test
    @DisplayName("특정 주식(들)의 현재 시세를 요청 시, DB 에서 값들을 가져온다.")
    void findStockPrices_byDB() {
        when(recentStockInfoCacheRepository.findByStockCode(anyString()))
                .thenReturn(Optional.empty());
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0);
        List<StockPriceCandle> candles = new ArrayList<>();
        for (long i = 1; i < 3; i++)
            candles.add(new StockPriceCandle(testStock, stockPrice, i));
        when(stockPriceCandleRepository.findTop2ByStockOrderByDateDesc(any(Stock.class)))
                .thenReturn(candles);

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("US1");
        stockCodes.add("US2");

        StockPricesResponse response = stockPriceService.findStockPrices(stockCodes);

        assertThat(response.prices().size()).isEqualTo(2);
        assertThat(response.prices().get(0).code()).isEqualTo("US1");
        assertThat(response.prices().get(1).code()).isEqualTo("US2");
        assertThat(response.prices().get(1).base()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("유효하지 않은 코드로 현재 주가(들)에 대한 간략한 정보를 불려오려고 하면, InvalidStockCodeException 을 발생시킨다.")
    void findStockPrices_exception_invalidCodes() {
        when(recentStockInfoCacheRepository.findByStockCode(anyString()))
                .thenThrow(new InvalidStockCodeException());

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("XXX");
        assertThatThrownBy(() -> stockPriceService.findStockPrices(stockCodes))
                .isInstanceOf(InvalidStockCodeException.class);
    }

    @Test
    @DisplayName("최근 3개월 동안의 주가 정보를 불러온다.")
    void findStockPriceCandles_3Months() {
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        List<StockPriceCandle> priceCandles = new ArrayList<>();
        StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0);
        int count = 10;
        for (int i = 0; i < count; i++)
            priceCandles.add(new StockPriceCandle(testStock, stockPrice, 1L));

        when(stockPriceCandleRepository.findAllByStockAndDateBetween(any(Stock.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(priceCandles);
        when(periodExtractor.getStart())
                .thenReturn(LocalDate.now());
        when(periodExtractor.getEnd())
                .thenReturn(LocalDate.now());

        StockPriceCandlesResponse response = stockPriceService.findStockPriceCandles("US1", periodExtractor);

        assertThat(response.candles().size()).isEqualTo(count);
    }

    @Test
    @DisplayName("CODE1 CODE2 에 대한 실시간 시세를 구독한다.")
    void subscribeStockPrices() throws IOException {
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        when(sseEmitterRepository.getSseEmitterByKey(anyString()))
                .thenReturn(Optional.of(sseEmitter));
        doNothing().when(sseEmitter)
                .send(any(SseEmitter.SseEventBuilder.class));

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("CODE1");
        stockCodes.add("CODE2");
        SseEmitter subscribedSseEmitter = stockPriceService.subscribeStockPrices(testAuthInfo, stockCodes);

        assertThat(subscribedSseEmitter).isEqualTo(sseEmitter);
    }

    @Test
    @DisplayName("실시간 시세 구독 시, 코드가 유효하지 않으면 StockNotFoundException 를 발생시킨다.")
    void subscribeStockPrices_invalidCode() {
        when(stockRepository.findByCode(anyString()))
                .thenThrow(new StockNotFoundException());

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("CODE1");
        stockCodes.add("CODE2");

        assertThatThrownBy(() -> stockPriceService.subscribeStockPrices(testAuthInfo, stockCodes))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    @DisplayName("SseEmitter 에 대한 키가 유효하지 않으면, SseEmitterNotFoundException 를 발생시킨다.")
    void subscribeStockPrices_exception_invalidKey() {
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        when(sseEmitterRepository.getSseEmitterByKey(anyString()))
                .thenThrow(new SseEmitterNotFoundException());

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("CODE1");
        stockCodes.add("CODE2");

        assertThatThrownBy(() -> stockPriceService.subscribeStockPrices(testAuthInfo, stockCodes))
                .isInstanceOf(SseEmitterNotFoundException.class);
    }

    @Test
    @DisplayName("SseEmitter 에서 이벤트 전송이 실패하면, SseEmitterEventSendException 를 발생시킨다.")
    void subscribeStockPrices_exception_sendEvent() throws IOException {
        when(stockRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStock));
        when(sseEmitterRepository.getSseEmitterByKey(anyString()))
                .thenReturn(Optional.of(sseEmitter));
        doThrow(new SseEmitterEventSendException()).when(sseEmitter)
                .send(any(SseEmitter.SseEventBuilder.class));

        List<String> stockCodes = new ArrayList<>();
        stockCodes.add("CODE1");
        stockCodes.add("CODE2");

        assertThatThrownBy(() -> stockPriceService.subscribeStockPrices(testAuthInfo, stockCodes))
                .isInstanceOf(SseEmitterEventSendException.class);
    }

    @Test
    @DisplayName("이벤트를 수신하면, 각 사용자들에게 전달한다.")
    void publishStockCurrentPrice() throws IOException {
        UpdateStockCurrentPriceEvent event = new UpdateStockCurrentPriceEvent(1L, "CODE", 1.0);
        Set<String> memberIds = new HashSet<>();
        memberIds.add("id1");
        when(sseEmitterRepository.getMemberIdsByStockId(anyLong()))
                .thenReturn(memberIds);
        when(sseEmitterRepository.getSseEmitterByKey(anyString()))
                .thenReturn(Optional.of(sseEmitter));

        stockPriceService.publishStockCurrentPrice(event);

        verify(sseEmitter).send(any(SseEmitter.SseEventBuilder.class));
    }

}