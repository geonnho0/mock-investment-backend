package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.SseEmitterEventSendException;
import org.mockInvestment.advice.exception.SseEmitterNotFoundException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stock.domain.RecentStockInfo;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.SseEmitterRepository;
import org.mockInvestment.stock.repository.RecentStockInfoCacheRepository;
import org.mockInvestment.stock.repository.StockPriceCandleRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.mockInvestment.stock.domain.UpdateStockCurrentPriceEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class StockPriceService {

    private final StockRepository stockRepository;

    private final RecentStockInfoCacheRepository recentStockInfoCacheRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final SseEmitterRepository sseEmitterRepository;


    public StockPriceService(StockRepository stockRepository, RecentStockInfoCacheRepository recentStockInfoCacheRepository, StockPriceCandleRepository stockPriceCandleRepository, SseEmitterRepository sseEmitterRepository) {
        this.stockRepository = stockRepository;
        this.recentStockInfoCacheRepository = recentStockInfoCacheRepository;
        this.stockPriceCandleRepository = stockPriceCandleRepository;
        this.sseEmitterRepository = sseEmitterRepository;
    }

    public StockPricesResponse findStockPrices(List<String> stockCodes) {
        List<StockPriceResponse> responses = new ArrayList<>();
        for (String code : stockCodes) {
            RecentStockInfo stockInfo = recentStockInfoCacheRepository.findByStockCode(code)
                    .orElseGet(() -> {
                        RecentStockInfo findStockInfo = findRecentStockInfo(code);
                        recentStockInfoCacheRepository.saveByCode(code, findStockInfo);
                        return findStockInfo;
                    });

            responses.add(StockPriceResponse.of(code, stockInfo));
        }
        return new StockPricesResponse(responses);
    }

    private RecentStockInfo findRecentStockInfo(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceCandle> stockPriceCandles = stockPriceCandleRepository.findTop2ByStockOrderByDateDesc(stock);
        StockPriceCandle recentCandle = stockPriceCandles.get(0);
        return new RecentStockInfo(stockPriceCandles.get(1).getClose(), stock, recentCandle);
    }

    public StockPriceCandlesResponse findStockPriceCandles(String stockCode, PeriodExtractor periodExtractor) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceCandle> priceCandles = stockPriceCandleRepository
                .findAllByStockAndDateBetween(stock, periodExtractor.getStart(), periodExtractor.getEnd());
        List<StockPriceCandleResponse> responses = priceCandles.stream()
                .map(StockPriceCandleResponse::new)
                .toList();
        return new StockPriceCandlesResponse(stockCode, responses);
    }

    public SseEmitter subscribeStockPrices(AuthInfo authInfo, List<String> stockCodes) {
        String key = authInfo.getId() + String.valueOf(System.currentTimeMillis());
        for (String stockCode : stockCodes) {
            Stock stock = stockRepository.findByCode(stockCode)
                    .orElseThrow(StockNotFoundException::new);
            sseEmitterRepository.createSubscription(key, stock.getId());
            sendToClient(key, new UpdateStockCurrentPriceEvent(stock.getId(), stockCode, 0.0));
        }
        return sseEmitterRepository.getSseEmitterByKey(key)
                .orElseThrow();
    }

    private void sendToClient(String key, UpdateStockCurrentPriceEvent updateStockCurrentPriceEvent) {
        SseEmitter emitter = sseEmitterRepository.getSseEmitterByKey(key)
                .orElseThrow(SseEmitterNotFoundException::new);

        try {
            emitter.send(SseEmitter.event()
                    .name("stock-price")
                    .id(key)
                    .data(updateStockCurrentPriceEvent));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitterRepository.deleteSseEmitterByKey(key);
            throw new SseEmitterEventSendException();
        }
    }

    @EventListener
    public void publishStockCurrentPrice(UpdateStockCurrentPriceEvent updateStockCurrentPriceEvent) {
        Set<String> memberIds = sseEmitterRepository.getMemberIdsByStockId(updateStockCurrentPriceEvent.stockId());
        for (String memberId : memberIds)
            sendToClient(memberId, updateStockCurrentPriceEvent);
    }

}
