package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.EmitterRepository;
import org.mockInvestment.stock.repository.LastStockInfoCacheRepository;
import org.mockInvestment.stock.repository.StockPriceCandleRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.mockInvestment.stockOrder.dto.StockCurrentPrice;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class StockPriceService {

    private final StockRepository stockRepository;

    private final LastStockInfoCacheRepository lastStockInfoCacheRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final EmitterRepository emitterRepository;


    public StockPriceService(StockRepository stockRepository, LastStockInfoCacheRepository lastStockInfoCacheRepository, StockPriceCandleRepository stockPriceCandleRepository, EmitterRepository emitterRepository) {
        this.stockRepository = stockRepository;
        this.lastStockInfoCacheRepository = lastStockInfoCacheRepository;
        this.stockPriceCandleRepository = stockPriceCandleRepository;
        this.emitterRepository = emitterRepository;
    }

    public StockPricesResponse findStockPrices(List<String> stockCodes) {
        List<StockPriceResponse> responses = new ArrayList<>();
        for (String code : stockCodes) {
            LastStockInfo stockInfo = lastStockInfoCacheRepository.findByStockCode(code)
                    .orElseThrow(JsonStringDeserializationFailureException::new);

            responses.add(StockPriceResponse.of(code, stockInfo));
        }
        return new StockPricesResponse(responses);
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
        for (String stockCode : stockCodes) {
            Stock stock = stockRepository.findByCode(stockCode)
                    .orElseThrow(StockNotFoundException::new);
            emitterRepository.createSubscription(authInfo.getId(), stock.getId());
            sendToClient(authInfo.getId(), new StockCurrentPrice(stock.getId(), stockCode, 0.0));
        }
        return emitterRepository.getSseEmitterByMemberId(authInfo.getId())
                .orElseThrow();
    }

    private void sendToClient(long memberId, StockCurrentPrice stockCurrentPrice) {
        SseEmitter emitter = emitterRepository.getSseEmitterByMemberId(memberId)
                .orElseThrow();

        try {
            emitter.send(SseEmitter.event()
                    .name("stock-price")
                    .data(stockCurrentPrice));
        } catch (IOException e) {
            emitter.completeWithError(e);
            emitterRepository.deleteSseEmitterByMemberId(memberId);
        }
    }

    @EventListener
    protected void publishStockCurrentPrice(StockCurrentPrice stockCurrentPrice) {
        Set<Long> memberIds = emitterRepository.getMemberIdsByStockId(stockCurrentPrice.stockId());
        for (Long memberId : memberIds)
            sendToClient(memberId, stockCurrentPrice);
    }

}
