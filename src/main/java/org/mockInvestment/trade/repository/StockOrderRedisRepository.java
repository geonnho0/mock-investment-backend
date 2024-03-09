package org.mockInvestment.trade.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class StockOrderRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public StockOrderRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Set<String> get(String key) {
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        return operations.members(key);
    }

    public void put(String key, String orderId) {
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        operations.add(key, orderId);
    }

    public void remove(String key, String value) {
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        operations.remove(key, value);
    }

}
