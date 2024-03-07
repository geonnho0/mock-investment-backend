package org.mockInvestment.config;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisSubService implements MessageListener {


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String notification = message.toString();
        System.out.println(notification);
    }
}
