package org.mockInvestment.stockOrder.repository;

import org.mockInvestment.advice.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.common.JsonStringMapper;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PendingStockOrderRedisRepository implements PendingStockOrderCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public PendingStockOrderRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(PendingStockOrder entity) {
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        String jsonString = JsonStringMapper.toJsonString(entity)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        operations.add(String.valueOf(entity.stockId()), jsonString);
    }

    @Override
    public List<PendingStockOrder> findAllByStockId(long stockId) {
        Set<String> jsonStrings = redisTemplate.opsForSet().members(String.valueOf(stockId));
        List<PendingStockOrder> pendingStockOrders = new ArrayList<>();
        if (jsonStrings == null || jsonStrings.isEmpty())
            return pendingStockOrders;

        for (String jsonString : jsonStrings) {
            Optional<PendingStockOrder> pendingStockOrder = JsonStringMapper.parseJsonString(jsonString, PendingStockOrder.class);
            if (pendingStockOrder.isEmpty())
                continue;
            pendingStockOrders.add(pendingStockOrder.get());
        }

        return pendingStockOrders;
    }

    @Override
    public void remove(PendingStockOrder entity) {
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        String jsonString = JsonStringMapper.toJsonString(entity)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        operations.remove(String.valueOf(entity.stockId()), jsonString);
    }

    @Override
    public Optional<PendingStockOrder> findByStockIdAndStockOrderId(long stockId, long stockOrderId) {
        Set<String> jsonStrings = redisTemplate.opsForSet().members(String.valueOf(stockId));
        if (jsonStrings == null || jsonStrings.isEmpty())
            return Optional.empty();

        for (String jsonString : jsonStrings) {
            Optional<PendingStockOrder> pendingStockOrder = JsonStringMapper.parseJsonString(jsonString, PendingStockOrder.class);
            if (pendingStockOrder.isEmpty())
                continue;
            if (pendingStockOrder.get().orderId() == stockOrderId) {
                return pendingStockOrder;
            }
        }

        return Optional.empty();
    }

}
