package org.mockInvestment.stock.repository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class StockRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public StockRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, String> get(String key) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        return operations.entries(key);
    }

    public void put(String key, String hashKey, String value) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        operations.put(key, hashKey, value);
    }
}
