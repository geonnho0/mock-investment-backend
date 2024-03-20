package org.mockInvestment.stockOrder.service;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.common.JsonStringMapper;
import org.mockInvestment.stockOrder.dto.StockCurrentPrice;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class PendingStockOrderService implements MessageListener {

    private final ApplicationEventPublisher applicationEventPublisher;


    public PendingStockOrderService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        StockCurrentPrice stockCurrentPrice = JsonStringMapper.parseJsonString(message.toString(), StockCurrentPrice.class)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        applicationEventPublisher.publishEvent(stockCurrentPrice);
    }

}
