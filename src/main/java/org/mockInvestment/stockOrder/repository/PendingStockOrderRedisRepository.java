package org.mockInvestment.stockOrder.repository;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.global.common.JsonStringMapper;
import org.mockInvestment.global.error.exception.JsonStringDeserializationFailureException;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class PendingStockOrderRedisRepository implements PendingStockOrderCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public void save(PendingStockOrder entity) {
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        String jsonString = JsonStringMapper.toJsonString(entity)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        operations.add(entity.code(), jsonString);
    }

    @Override
    public List<PendingStockOrder> findAllByStockCode(String stockCode) {
        Set<String> jsonStrings = get(stockCode);
        return parseJsonStrings(jsonStrings);
    }

    @Override
    public List<PendingStockOrder> findAllByMemberId(Long memberId) {
        Set<String> stockCodes = findAllRedisKeys();
        List<PendingStockOrder> pendingStockOrders = new ArrayList<>();
        for (String stockCode : stockCodes) {
            pendingStockOrders.addAll(findAllByStockCodeAndMemberId(stockCode, memberId));
        }

        return pendingStockOrders;
    }

    @Override
    public void delete(PendingStockOrder entity) {
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        String jsonString = JsonStringMapper.toJsonString(entity)
                .orElseThrow(JsonStringDeserializationFailureException::new);
        operations.remove(entity.code(), jsonString);
        deleteKeyIfEmpty(entity.code());
    }

    @Override
    public void deleteById(Long stockOrderId) {
        Optional<PendingStockOrder> pendingStockOrder = findById(stockOrderId);
        pendingStockOrder.ifPresent(this::delete);
    }

    @Override
    public void deleteAll() {
        Set<String> stockCodes = findAllRedisKeys();
        for (String stockCode : stockCodes) {
            redisTemplate.delete(stockCode);
        }
    }

    private Set<String> findAllRedisKeys() {
        Set<String> stockCodes = redisTemplate.keys("*");
        if (stockCodes == null || stockCodes.isEmpty())
            return Set.of();
        return stockCodes;
    }

    private List<PendingStockOrder> findAllByStockCodeAndMemberId(String stockCode, Long memberId) {
        return findAllByStockCode(stockCode).stream()
                .filter(pendingStockOrder -> pendingStockOrder.orderedBy(memberId))
                .toList();
    }

    private Optional<PendingStockOrder> findById(Long stockOrderId) {
        List<PendingStockOrder> pendingStockOrders = findAll();
        return pendingStockOrders.stream()
                .filter(pendingStockOrder -> Objects.equals(pendingStockOrder.id(), stockOrderId))
                .findFirst();
    }

    private List<PendingStockOrder> findAll() {
        Set<String> stockCodes = findAllRedisKeys();
        List<PendingStockOrder> pendingStockOrders = new ArrayList<>();
        for (String stockCode : stockCodes) {
            Set<String> jsonStrings = get(stockCode);
            pendingStockOrders.addAll(parseJsonStrings(jsonStrings));
        }
        return pendingStockOrders;
    }

    private Set<String> get(String redisKey) {
        return redisTemplate.opsForSet().members(redisKey);
    }

    private List<PendingStockOrder> parseJsonStrings(Set<String> jsonStrings) {
        if (jsonStrings.isEmpty())
            return List.of();

        return jsonStrings.stream()
                .map((jsonString) -> JsonStringMapper.parseJsonString(jsonString, PendingStockOrder.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private void deleteKeyIfEmpty(String redisKey) {
        Set<String> jsonStrings = get(redisKey);
        if (jsonStrings == null || jsonStrings.isEmpty())
            redisTemplate.delete(redisKey);
    }

}
