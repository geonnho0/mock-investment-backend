package org.mockInvestment.stock.repository;

import org.mockInvestment.advice.exception.InvalidStockCodeException;
import org.mockInvestment.common.JsonStringMapper;
import org.mockInvestment.stock.dto.LastStockInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LastStockInfoRedisRepository implements LastStockInfoCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public LastStockInfoRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<LastStockInfo> findByStockCode(String code) {
        String jsonString = redisTemplate.opsForValue().get(code);
        if (jsonString == null)
            throw new InvalidStockCodeException();
        return JsonStringMapper.parseJsonString(jsonString, LastStockInfo.class);
    }

}
