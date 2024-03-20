package org.mockInvestment.config;

import org.mockInvestment.stockOrder.service.PendingStockOrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final RedisConnectionFactory redisConnectionFactory;

    private final String stockCurrentPriceTopic;

    public RedisPubSubConfig(ApplicationEventPublisher applicationEventPublisher, RedisConnectionFactory redisConnectionFactory,
                             @Value("${spring.redis.topic.stockPriceUpdateChannel}") String stockCurrentPriceTopic) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.redisConnectionFactory = redisConnectionFactory;
        this.stockCurrentPriceTopic = stockCurrentPriceTopic;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new PendingStockOrderService(applicationEventPublisher));
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter(), stockCurrentPriceTopic());
        return container;
    }

    @Bean
    public ChannelTopic stockCurrentPriceTopic() {
        return new ChannelTopic(stockCurrentPriceTopic);
    }
}
