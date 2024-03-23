package org.mockInvestment.stock.repository;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.common.JsonStringMapper;
import org.mockInvestment.stock.domain.RecentStockInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RecentStockInfoRedisRepository implements RecentStockInfoCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public RecentStockInfoRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<RecentStockInfo> findByStockCode(String code) {
        String jsonString = redisTemplate.opsForValue().get(code);
        if (jsonString == null)
            return Optional.empty();
        return JsonStringMapper.parseJsonString(jsonString, RecentStockInfo.class);
    }

    @Override
    public void saveByCode(String code, RecentStockInfo entity) {
        String jsonString = JsonStringMapper.toJsonString(entity)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        redisTemplate.opsForValue().set(code, jsonString, 60, TimeUnit.SECONDS);
    }

}
