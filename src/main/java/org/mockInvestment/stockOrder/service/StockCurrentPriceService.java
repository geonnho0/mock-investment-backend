package org.mockInvestment.stockOrder.service;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.common.JsonStringMapper;
import org.mockInvestment.stock.domain.UpdateStockCurrentPriceEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class StockCurrentPriceService implements MessageListener {

    private final ApplicationEventPublisher applicationEventPublisher;


    public StockCurrentPriceService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UpdateStockCurrentPriceEvent updateStockCurrentPriceEvent = JsonStringMapper.parseJsonString(message.toString(), UpdateStockCurrentPriceEvent.class)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        applicationEventPublisher.publishEvent(updateStockCurrentPriceEvent);
    }

}
