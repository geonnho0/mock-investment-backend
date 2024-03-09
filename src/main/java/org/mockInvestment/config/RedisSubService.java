package org.mockInvestment.config;

import org.mockInvestment.advice.exception.StockOrderNotFoundException;
import org.mockInvestment.trade.domain.StockOrder;
import org.mockInvestment.trade.repository.StockOrderRedisRepository;
import org.mockInvestment.trade.repository.StockOrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RedisSubService implements MessageListener {

    private final ApplicationEventPublisher applicationEventPublisher;


    public RedisSubService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        applicationEventPublisher.publishEvent(message.toString());
    }
}
